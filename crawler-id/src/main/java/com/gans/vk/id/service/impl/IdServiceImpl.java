package com.gans.vk.id.service.impl;

import static com.gans.vk.context.SystemProperties.NumericProperty.CRAWLER_ID_MIN_AUDIO_LIB_SIZE;
import static com.gans.vk.context.SystemProperties.Property.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.GroupInfo;
import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.id.dao.IdDao;
import com.gans.vk.id.dao.impl.IdDaoImpl;
import com.gans.vk.id.service.IdService;
import com.gans.vk.id.statistic.GroupStatistics;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.RestUtils;

public class IdServiceImpl implements IdService {

    private static final Log LOG = LogFactory.getLog(IdServiceImpl.class);

    private IdDao _idDao;
    private HttpVkConnector _httpVkConnector;

    private static IdService _idService = new IdServiceImpl();

    private IdServiceImpl() {
        _idDao = IdDaoImpl.getInstance();
        _httpVkConnector = HttpVkConnector.getInstance();
    }

    public static IdService getInstance() {
        return _idService;
    }

    @Override
    public List<String> getExistingIds() {
        String path = SystemProperties.get(CRAWLER_ID_STASH);
        return _idDao.getAllIds(path);
    }

    @Override
    public List<String> getGroups() {
        String path = SystemProperties.get(CRAWLER_GROUP_STASH);
        return _idDao.getGroups(path);
    }

    @Override
    public List<GroupInfo> getGroupInfos(List<String> groups) {
        List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
        for (String groupUrl : groups) {
            LOG.info(MessageFormat.format("Processing group: {0}", groupUrl));
            GroupInfo groupInfo = getGroupInfo(groupUrl);
            if (groupInfo.isValid()) {
                groupInfos.add(groupInfo);
            }
            RestUtils.sleep();
        }
        return groupInfos;
    }

    private GroupInfo getGroupInfo(String groupUrl) {
        String html = _httpVkConnector.get(groupUrl);
        html = HtmlUtils.sanitizeHtml(html);
        if (StringUtils.isEmpty(html)) {
            return new GroupInfo();
        } else {
            return extractGroupInfoFromPage(html);
        }
    }

    private GroupInfo extractGroupInfoFromPage(String html) {
        final String GROUP_INFO_CONTAINER_ID = "group_followers";
        final String GROUP_ID_CONTAINER_CLASS = "module_header";
        final String GROUP_MEMBERS_COUNT_CONTAINER_CLASS = "p_header_bottom";
        final String LOGIN_FORM_ID = "quick_login_form";

        Document doc = Jsoup.parse(html);
        if (doc.getElementById(LOGIN_FORM_ID) != null) {
            LOG.info("Parsing GroupInfo fails: user must be login into system to continue");
            return new GroupInfo();
        }

        Element infoContainer = doc.getElementById(GROUP_INFO_CONTAINER_ID);
        if (infoContainer == null) {
            LOG.info(MessageFormat.format("Parsing GroupInfo fails: unexpected HTML response. Element wiht id {0} not found", GROUP_INFO_CONTAINER_ID));
            return new GroupInfo();
        }

        GroupInfo result = new GroupInfo();

        // group id
        for (Element element : infoContainer.getElementsByClass(GROUP_ID_CONTAINER_CLASS)) {
            String groupId = parseGroupId(element.attr("href"));
            result.setGroupId(groupId);
        }

        // group members count
        for (Element element : infoContainer.getElementsByClass(GROUP_MEMBERS_COUNT_CONTAINER_CLASS)) {
            int membersCount = extractNumericValue(element.text());
            result.setMembersCount(membersCount);
        }

        return result;
    }

    private String parseGroupId(String attr) {
        Matcher matcher = Pattern.compile("\\[group\\]=(\\d+)").matcher(attr);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private int extractNumericValue(String text) {
        String count = text.replaceAll("\\D", "");
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public List<String> discoverNewIds(GroupInfo groupInfo) {
        LOG.info(MessageFormat.format("Collect members id from group: {0}", groupInfo.toString()));

        List<String> membersUrlsBucket = new ArrayList<String>();

        int offset = 0;
        final int PEOPLE_ON_PAGE = 60;
        final int PEOPLE_ON_FIRST_PAGE = 120;
        while (offset < groupInfo.getMembersCount()) {
            List<String> membersUrls = getGroupMembersUrls(groupInfo.getGroupId(), offset);
            membersUrlsBucket.addAll(membersUrls);

            if (offset > 0) {
                offset += PEOPLE_ON_PAGE;
            } else if (offset == 0) {
                offset += PEOPLE_ON_FIRST_PAGE;
            }

            RestUtils.sleep();
        }

        List<String> ids = new ArrayList<String>();
        GroupStatistics groupStatistics = new GroupStatistics(groupInfo);
        for (String memberUrl : membersUrlsBucket) {
            String id = lookupMemberId(memberUrl, groupStatistics);
            if (StringUtils.isNotEmpty(id)) {
                ids.add(id);
            }
            RestUtils.sleep();
        }

        LOG.info(groupStatistics.getGroupStatistics());

        return ids;
    }

    private List<String> getGroupMembersUrls(String groupId, int offset) {
        final String GROUP_MEMBER_CONTAINER_CLASS = "fans_fan_name";
        final String GROUP_MEMBER_LINK_ELEMENT_SELECTOR = "a.fans_fan_lnk";

        String url = SystemProperties.get(VK_GROUP_MEMBERS_URL);
        String postEntity = MessageFormat.format(SystemProperties.get(VK_GROUP_MEMBERS_ENTITY_PATTERN), offset, groupId);

        String html = _httpVkConnector.post(url, postEntity);
        html = HtmlUtils.sanitizeHtml(html);
        if (StringUtils.isEmpty(html)) {
            return Collections.emptyList();
        }

        List<String> membersUrls = new LinkedList<String>();
        Document doc = Jsoup.parse(html);
        for (Element element : doc.getElementsByClass(GROUP_MEMBER_CONTAINER_CLASS)) {
            String href = element.select(GROUP_MEMBER_LINK_ELEMENT_SELECTOR).iterator().next().attr("href");
            if (StringUtils.isNotEmpty(href)) {
                membersUrls.add(href);
            }
        }
        return membersUrls;
    }

    private String lookupMemberId(String memberUrl, GroupStatistics groupStatistics) {
        LOG.trace(MessageFormat.format("Discover member id by url: {0}", memberUrl));

        if (StringUtils.isEmpty(memberUrl)) {
            groupStatistics.parserError();
            return "";
        }

        final String AUDIO_COMPONENT_ID = "profile_audios";
        final String ID_LINK_SELECTOR = "a.module_header";
        final String AUDIO_COUNT_COMPONENT_CLASS = "p_header_bottom";

        String domain = SystemProperties.get(VK_DOMAIN, "vk.com/");
        String url = memberUrl.startsWith("/") ? domain + memberUrl.substring(1) : domain + memberUrl;
        String html = _httpVkConnector.get(url);
        html = HtmlUtils.sanitizeHtml(html);

        Document doc = Jsoup.parse(html);
        Element audios = doc.getElementById(AUDIO_COMPONENT_ID);
        if (audios == null) {
            groupStatistics.closedPage();
            return "";
        }

        for (Element element : audios.getElementsByClass(AUDIO_COUNT_COMPONENT_CLASS)) {
            String audioSize = element.text();
            int size = extractNumericValue(audioSize);
            if (size < SystemProperties.get(CRAWLER_ID_MIN_AUDIO_LIB_SIZE)) {
                groupStatistics.notEnoughAudio();
                return "";
            }
        }

        String href = audios.select(ID_LINK_SELECTOR).attr("href");
        if (StringUtils.isEmpty(href)) {
            groupStatistics.parserError();
            return "";
        }

        String id = href.replaceAll("\\D", "");
        if (StringUtils.isEmpty(id)) {
            groupStatistics.parserError();
            return "";
        }

        return id;
    }

    @Override
    public void addIds(List<String> newIds) {
        // TODO Auto-generated method stub
    }
}
