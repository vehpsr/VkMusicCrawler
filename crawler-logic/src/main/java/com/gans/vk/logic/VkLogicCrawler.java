package com.gans.vk.logic;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.data.RecommendedArtistsData;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.service.LogicService;
import com.gans.vk.logic.service.impl.LogicServiceImpl;
import com.gans.vk.logic.service.impl.OutputFormatter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class VkLogicCrawler {

    private static final Log LOG = LogFactory.getLog(VkLogicCrawler.class);

    public static void main(String[] args) {
        LOG.info("debug: " + SystemProperties.debug());
        new VkLogicCrawler().start();
        LOG.info("done");
    }

    private LogicService _logicService;

    private VkLogicCrawler() {
        _logicService = LogicServiceImpl.getInstance();
    }

    private void start() {
        List<AudioProcessor> processors = _logicService.getProcessors();
        List<String> files = _logicService.getAllAudioFiles();

        Multimap<String, Entry<AudioProcessor, Number>> metrics = ArrayListMultimap.create();
        for (String file : files) {
            AudioLibrary lib = _logicService.getLibrary(file);
            for (AudioProcessor processor : processors) {
                Entry<String, Number> entry = processor.evaluate(lib);
                metrics.put(entry.getKey(), new AbstractMap.SimpleEntry<AudioProcessor, Number>(processor, entry.getValue()));
            }
        }

        List<Entry<String, Double>> aggregatedData = _logicService.getAggregatedMetricData(metrics);
        List<RecommendedArtistsData> recommendedWhiteListArtists = _logicService.recommendedWhiteListArtists(aggregatedData);
        List<RecommendedArtistsData> recommendedBlackListArtists = _logicService.recommendedBlackListArtists(aggregatedData);
        List<RecommendedArtistsData> recommendedBlackWithoutWhiteListArtists = _logicService.recommendedBlackWithoutWhiteListArtists(aggregatedData);

        List<String> statistics = OutputFormatter.format(aggregatedData, metrics, recommendedWhiteListArtists, recommendedBlackListArtists, recommendedBlackWithoutWhiteListArtists);

        _logicService.save(statistics);
    }

}