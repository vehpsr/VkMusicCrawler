package com.gans.vk.id.service.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_GROUP_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_ID_STASH;
import static com.gans.vk.context.SystemProperties.Property.VK_GROUP_MEMBERS_ENTITY_PATTERN;
import static com.gans.vk.context.SystemProperties.Property.VK_GROUP_MEMBERS_URL;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import com.gans.vk.id.data.IdDao;
import com.gans.vk.id.data.impl.IdDaoImpl;
import com.gans.vk.id.service.IdService;
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
            int membersCount = parseGroupMembersCount(element.text());
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

    private int parseGroupMembersCount(String text) {
        String count = text.replaceAll("\\D", "");
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public void discoverNewIds(GroupInfo groupInfo) {
        LOG.info(MessageFormat.format("Collect members id from group: {0}", groupInfo.toString()));

        List<String> existingIds = getExistingIds();
        Collections.sort(existingIds);

        int offset = 0;
        int peopleOnPage = 60;
        while (offset < groupInfo.getMembersCount()) {
            List<String> rawUrls = getGroupMembersUrls(groupInfo.getGroupId(), offset);
            for (String rawUrl : rawUrls) {

            }
            offset += peopleOnPage;
        }
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

        List<String> rawUrls = new LinkedList<String>();
        Document doc = Jsoup.parse(html);
        for (Element element : doc.getElementsByClass(GROUP_MEMBER_CONTAINER_CLASS)) {
            String href = element.select(GROUP_MEMBER_LINK_ELEMENT_SELECTOR).iterator().next().attr("href");
            if (StringUtils.isNotEmpty(href)) {
                rawUrls.add(href);
            }
        }
        return rawUrls;
    }

    @Override
    public void addIds(List<String> newIds) {
        // TODO Auto-generated method stub
    }
}
