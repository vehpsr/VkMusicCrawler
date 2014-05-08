package com.gans.vk.audio;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.audio.service.AudioService;
import com.gans.vk.audio.service.impl.AudioServiceImpl;
import com.gans.vk.context.SystemProperties;

public class VkAudioCrawler {

    private static final Log LOG = LogFactory.getLog(VkAudioCrawler.class);

    public static void main(String[] args) {
        LOG.info("debug: " + SystemProperties.debug());
        new VkAudioCrawler().start();
    }

    private AudioService _audioService;

    private VkAudioCrawler() {
        _audioService = AudioServiceImpl.getInstance();
    }

    private void start() {
        Collection<String> ids = _audioService.getIdsForAudioDiscovery();
        if (CollectionUtils.isEmpty(ids)) {
            LOG.info("No new ID for audio discovery");
            return;
        }
    }
}
