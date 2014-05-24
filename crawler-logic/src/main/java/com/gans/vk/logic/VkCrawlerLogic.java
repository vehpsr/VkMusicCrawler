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
        List<String> files = _logicService.getAllAudioFiles();

        Multimap<String, Entry<AudioProcessor, Metric>> metrics = ArrayListMultimap.create();
        for (String file : files) {
            AudioLibrary lib = _logicService.getLibrary(file);
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
                builder.append(getMetricName(entry.getKey()) + " \t " + entry.getValue() + " \t ");
            }
            builder.append(id);
            statistics.add(builder.toString());
        }
        _logicService.save(statistics);
    }

	private String getMetricName(AudioProcessor processor) {
		final String AUDIO_PROCESSOR_SUFFIX = "AP";
		String acronim = processor.getClass().getSimpleName().replaceAll("[a-z]", "");
		return acronim.substring(0, acronim.lastIndexOf(AUDIO_PROCESSOR_SUFFIX));
	}
}