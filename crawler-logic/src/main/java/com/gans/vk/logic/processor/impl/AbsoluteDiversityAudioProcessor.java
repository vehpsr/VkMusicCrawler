package com.gans.vk.logic.processor.impl;

import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.DiversityMetric;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.processor.AudioProcessor;

public class AbsoluteDiversityAudioProcessor implements AudioProcessor {

    @Override
    public String getDescription() {
        return "Absolute diversity processor: percentage of unique artist in library to total library size.";
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        float diversity = (float) lib.getUniqueEntriesCount() / lib.getTotalEntriesCount();
        return new AbstractMap.SimpleEntry<String, Metric>(lib.getId(), new DiversityMetric(diversity));
    }

}
