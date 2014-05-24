package com.gans.vk.logic;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.AudioLibrary;
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

        Multimap<String, Entry<AudioProcessor, Number>> metrics = ArrayListMultimap.create();
        for (String file : files) {
            AudioLibrary lib = _logicService.getLibrary(file);
            for (AudioProcessor processor : processors) {
                Entry<String, Number> entry = processor.evaluate(lib);
                metrics.put(entry.getKey(), new AbstractMap.SimpleEntry<AudioProcessor, Number>(processor, entry.getValue()));
            }
        }

        List<Entry<String, Double>> aggregatedData = _logicService.getAggregatedMetricData(metrics);

        List<String> statistics = formatOutputData(aggregatedData, metrics);

        _logicService.save(statistics);
    }

    private List<String> formatOutputData(List<Entry<String, Double>> aggregatedData, Multimap<String, Entry<AudioProcessor, Number>> metrics) {
        if (metrics.isEmpty() || aggregatedData.isEmpty()) {
            return Collections.emptyList();
        }

        // total count
        List<String> result = new LinkedList<String>();
        result.add("Process total amount of audio libraries: " + aggregatedData.size());
        result.add("\nTop 10 of audio libraries.");

        // header
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("id\t\t");
        headerBuilder.append("AggM\t");
        String first = metrics.keySet().iterator().next();
        for (Entry<AudioProcessor, Number> entry : metrics.get(first)) {
            headerBuilder.append(entry.getKey().metricName()).append("\t");
        }
        result.add(headerBuilder.toString());

        // top 10 audio libraries
        result.addAll(topTen(aggregatedData, metrics));

        // top worst libraries
        result.add("\nThis you might consider to add to BlackList");
        Collections.reverse(aggregatedData);
        result.addAll(topTen(aggregatedData, metrics));

        //legend
        result.add("\nLegend:");
        for (Entry<AudioProcessor, Number> entry : metrics.get(first)) {
            result.add(entry.getKey().metricName() + " - " + entry.getKey().metricDescription());
        }

        return result;
    }

    private Collection<? extends String> topTen(List<Entry<String, Double>> aggregatedData, Multimap<String, Entry<AudioProcessor, Number>> metrics) {
        List<String> result = new LinkedList<String>();
        final int SIZE = metrics.size() < 10 ? metrics.size() : 10;
        int count = 0;
        for (Entry<String, Double> data : aggregatedData) {
            StringBuilder row = new StringBuilder();
            row.append(data.getKey()).append("\t");
            row.append(data.getValue()).append("\t");
            Collection<Entry<AudioProcessor, Number>> entries = metrics.get(data.getKey());
            for (Entry<AudioProcessor, Number> entry : entries) {
                row.append(entry.getValue()).append("\t");
            }
            result.add(row.toString());

            count++;
            if (count > SIZE) {
                break;
            }
        }
        return result;
    }
}