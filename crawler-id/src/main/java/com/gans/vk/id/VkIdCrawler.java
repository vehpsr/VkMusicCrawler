package com.gans.vk.id;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.data.GroupInfo;
import com.gans.vk.id.service.IdService;
import com.gans.vk.id.service.impl.IdServiceImpl;

public class VkIdCrawler {

    private static final Log LOG = LogFactory.getLog(VkIdCrawler.class);

    public static void main(String[] args) {
        new VkIdCrawler().start();
    }

    private IdService _idService;

    private VkIdCrawler() {
        _idService = IdServiceImpl.getInstance();
    }

    private void start() {
        List<String> groups = _idService.getGroups();
        if (CollectionUtils.isEmpty(groups)) {
            LOG.info("No groups found for members ID discovery");
            return;
        }

        List<GroupInfo> groupInfos = _idService.getGroupInfos(groups);
        for (GroupInfo groupInfo : groupInfos) {
            _idService.discoverNewIds(groupInfo);
        }
    }
}
