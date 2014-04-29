package com.gans.vk.id.service.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_GROUP_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_ID_STASH;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.context.SystemProperties.Property;
import com.gans.vk.data.GroupInfo;
import com.gans.vk.httpclient.HttpConnector;
import com.gans.vk.id.data.IdDao;
import com.gans.vk.id.data.impl.IdDaoImpl;
import com.gans.vk.id.service.IdService;
import com.gans.vk.utils.RestUtils;

public class IdServiceImpl implements IdService {

    private static final Log LOG = LogFactory.getLog(IdServiceImpl.class);

    private IdDao _idDao;
    private HttpClient _httpClient;

    private static IdService _idService = new IdServiceImpl();

    private IdServiceImpl() {
        _idDao = IdDaoImpl.getInstance();
        _httpClient = HttpConnector.getInstance();
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
        GroupInfo result = new GroupInfo();

        HttpGet httpGet = new HttpGet(groupUrl);
        httpGet.setHeader("Cookie", SystemProperties.get(Property.VK_HEADER_COOKIES));
        httpGet.setHeader("User-Agent", SystemProperties.get(Property.VK_HEADER_USER_AGENT));
        try {
            HttpResponse response = _httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error(MessageFormat.format("Fail to reach {0}, response: {1}", groupUrl, response.getStatusLine().getStatusCode()));
                return result;
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String html = EntityUtils.toString(entity);
                result = extractGroupInfoFromPage(html);
            }
        } catch (Exception e) {
            if (e instanceof ClientProtocolException || e instanceof IOException) {
                LOG.error(e.getMessage());
            } else {
                throw new IllegalStateException("System error", e);
            }
        }
        return result;
    }

    private GroupInfo extractGroupInfoFromPage(String html) {
        GroupInfo result = new GroupInfo();

        if (StringUtils.isEmpty(html)) {
            LOG.info("Parsing GroupInfo fails: invalid response");
            return result;
        }

        final String GROUP_INFO_CONTAINER_ID = "group_followers";
        final String GROUP_ID_CONTAINER_CLASS = "module_header";
        final String GROUP_MEMBERS_COUNT_CONTAINER_CLASS = "p_header_bottom";
        final String LOGIN_FORM_ID = "quick_login_form";

        Document doc = Jsoup.parse(html);
        if (doc.getElementById(LOGIN_FORM_ID) != null) {
            LOG.info("Parsing GroupInfo fails: user must be login into system to continue");
            return result;
        }

        Element infoContainer = doc.getElementById(GROUP_INFO_CONTAINER_ID);
        if (infoContainer == null) {
            LOG.info(MessageFormat.format("Parsing GroupInfo fails: unexpected HTML response. Element wiht id {0} not found", GROUP_INFO_CONTAINER_ID));
            return result;
        }

        // group id
        for (Element element : infoContainer.getElementsByClass(GROUP_ID_CONTAINER_CLASS)) {
            String groupId = parseGroupId(element.attr("href"));
            result.setGroupId(groupId);
        }

        // group members count
        for (Element element : infoContainer.getElementsByClass(GROUP_MEMBERS_COUNT_CONTAINER_CLASS)) {
            int groupMembers = parseGroupMembers(element.text());
            result.setGroupMembers(groupMembers);
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

    private int parseGroupMembers(String text) {
        String count = text.replaceAll("\\D", "");
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public List<String> getNewIds(GroupInfo groupInfo, List<String> existingIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addIds(List<String> newIds) {
        // TODO Auto-generated method stub
    }
}
