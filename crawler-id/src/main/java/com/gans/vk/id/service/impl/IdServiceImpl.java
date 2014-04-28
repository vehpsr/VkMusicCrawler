package com.gans.vk.id.service.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_GROUP_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_ID_STASH;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import com.gans.vk.httpclient.HttpConnector;
import com.gans.vk.id.data.IdDao;
import com.gans.vk.id.data.impl.IdDaoImpl;
import com.gans.vk.id.service.IdService;

public class IdServiceImpl implements IdService {

    private static final Log LOG = LogFactory.getLog(IdServiceImpl.class);

    private IdDao _idDao;
    private HttpClient _httpClient;

    private static IdService _idService = new IdServiceImpl();

    private IdServiceImpl() { /* singleton */
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
    public List<String> discoverNewIds(List<String> groups, List<String> existingIds) {
        List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
        for (String groupUrl : groups) {
            GroupInfo groupInfo = getGroupInfo(groupUrl);
            if (!groupInfo.isInvalid()) {
                groupInfos.add(groupInfo);
            }
        }
        List<String> newIds = new LinkedList<String>();
        return newIds;
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
            } else {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String html = EntityUtils.toString(entity);
                    Document doc = Jsoup.parse(html);
                    // TODO check if user logged
                    Element infoContainer = doc.getElementById("group_followers");
                    // TODO parse infoContainer
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        return result;
    }

    private class GroupInfo {
        private String _groupId;
        private int _groupMembers;

        private boolean isInvalid() {
            return StringUtils.isEmpty(_groupId) || _groupMembers < 1;
        }
    }
}
