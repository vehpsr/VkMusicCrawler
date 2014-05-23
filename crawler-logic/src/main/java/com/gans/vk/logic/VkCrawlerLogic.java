package com.gans.vk.logic;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.service.LogicService;
import com.gans.vk.logic.service.impl.LogicServiceImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
        List<AudioProcessor> processors = _logicService.getProcessors();
        List<AudioLibrary> allAudio = _logicService.getAllAudio();

        Multimap<String, Entry<AudioProcessor, Metric>> metrics = ArrayListMultimap.create();
        for (AudioLibrary lib : allAudio) {
            for (AudioProcessor processor : processors) {
                Entry<String, Metric> entry = processor.evaluate(lib);
                metrics.put(entry.getKey(), new AbstractMap.SimpleEntry<AudioProcessor, Metric>(processor, entry.getValue()));
            }
        }

        List<String> statistics = new LinkedList<String>();
        for (String id : metrics.keySet()) {
            StringBuilder builder = new StringBuilder();
            Collection<Entry<AudioProcessor, Metric>> entries = metrics.get(id);
            for (Entry<AudioProcessor, Metric> entry : entries) {
                builder.append(entry.getKey().getClass().getSimpleName() + " \t " + entry.getValue() + " \t ");
            }
            builder.append(id);
            statistics.add(builder.toString());
        }
        _logicService.save(statistics);
    }
}