package com.gans.vk.logic.processor.impl;

import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;

public class AbsoluteDiversityAudioProcessor implements AudioProcessor {

    @Override
    public String metricDescription() {
        return "Absolute diversity processor: percentage of unique artist in library to total library size.";
    }

    @Override
    public Entry<String, Number> evaluate(AudioLibrary lib) {
        float diversity = (float) lib.getUniqueEntriesCount() / lib.getTotalEntriesCount() * 100;
        return new AbstractMap.SimpleEntry<String, Number>(lib.getId(), diversity);
    }

    @Override
    public double aggregationValue() {
        return 0.1;
    }

    @Override
    public String metricName() {
        return "AbsDiv-ty";
    }

}
