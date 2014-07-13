package com.gans.vk.logic.service.impl;

import static com.gans.vk.context.SystemProperties.NumericProperty.CRAWLER_AUDIO_TOP_ARTISTS_COUNT;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.RecommendedArtistsData;
import com.gans.vk.logic.processor.AudioProcessor;
import com.google.common.collect.Multimap;

public class OutputFormatter {

     private static final DecimalFormat FORMATER = new DecimalFormat("#.##");

     public static List<String> format(List<Entry<String, Double>> aggregatedData, Multimap<String, Entry<AudioProcessor, Number>> metrics, List<RecommendedArtistsData> recommendedArtists, List<RecommendedArtistsData> recommendedBlackListArtists, List<RecommendedArtistsData> recommendedBlackWithoutWhiteListArtists) {
        if (metrics.isEmpty() || aggregatedData.isEmpty()) {
            return Collections.emptyList();
        }

        // total count
        List<String> result = new LinkedList<String>();
        result.add("Process total amount of audio libraries: " + aggregatedData.size());
        result.add("\nTop audio libraries.");

        // header
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("    id\t");
        headerBuilder.append("AggM\t");
        String first = metrics.keySet().iterator().next();
        for (Entry<AudioProcessor, Number> entry : metrics.get(first)) {
            headerBuilder.append(entry.getKey().metricName()).append("\t");
        }
        result.add(headerBuilder.toString());

        // top best audio libraries
        int topArtistsCount = SystemProperties.get(CRAWLER_AUDIO_TOP_ARTISTS_COUNT);
        result.addAll(top(topArtistsCount, aggregatedData, metrics));

        // recommended for black list unique (without white list artists)
        result.add("\nYou might consider add this artists to Black or White list");
        for (RecommendedArtistsData recommendedArtist : recommendedBlackWithoutWhiteListArtists) {
            result.add(recommendedArtist.format());
        }

        // recommended white list artists
        result.add("\nRecommended artists for you are:");
        for (RecommendedArtistsData recommendedArtist : recommendedArtists) {
            result.add(recommendedArtist.format());
        }

        // recommended for black list
        result.add("\nYou might consider add this artists to BlackList");
        for (RecommendedArtistsData recommendedArtist : recommendedBlackListArtists) {
            result.add(recommendedArtist.format());
        }

        // top worst libraries
        result.add("\nThis you might consider to add to BlackList");
        Collections.reverse(aggregatedData);
        result.addAll(top(20, aggregatedData, metrics));

        //legend
        result.add("\nLegend:");
        for (Entry<AudioProcessor, Number> entry : metrics.get(first)) {
            result.add(entry.getKey().metricName() + " - " + entry.getKey().metricDescription());
        }

        return result;
    }

    private static Collection<? extends String> top(int topCount, List<Entry<String, Double>> aggregatedData, Multimap<String, Entry<AudioProcessor, Number>> metrics) {
        List<String> result = new LinkedList<String>();
        final int SIZE = metrics.size() < topCount ? metrics.size() : topCount;
        int count = 0;
        for (Entry<String, Double> data : aggregatedData) {
            StringBuilder row = new StringBuilder();
            row.append(data.getKey()).append("\t");
            row.append(FORMATER.format(data.getValue())).append("\t");
            Collection<Entry<AudioProcessor, Number>> entries = metrics.get(data.getKey());
            for (Entry<AudioProcessor, Number> entry : entries) {
                row.append(FORMATER.format(entry.getValue())).append("\t");
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
