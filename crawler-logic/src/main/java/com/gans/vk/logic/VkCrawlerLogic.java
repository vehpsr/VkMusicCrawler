package com.gans.vk.logic;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.ArtistData;
import com.gans.vk.logic.service.LogicService;
import com.gans.vk.logic.service.impl.LogicServiceImpl;

public class VkCrawlerLogic {

    private static final Log LOG = LogFactory.getLog(VkCrawlerLogic.class);

    public static void main(String[] args) {
        LOG.info("debug: " + SystemProperties.debug());
        new VkCrawlerLogic().start();
    }

    private LogicService _logicService;

    private VkCrawlerLogic() {
        _logicService = LogicServiceImpl.getInstance();
    }

    private void start() {
        List<ArtistData> whiteList = _logicService.getWhiteList();
        List<ArtistData> blackList = _logicService.getBlackList();
    }
}