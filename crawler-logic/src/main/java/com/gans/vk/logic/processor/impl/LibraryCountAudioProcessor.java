package com.gans.vk.logic.processor.impl;

import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;

public class LibraryCountAudioProcessor implements AudioProcessor {

    @Override
    public String metricDescription() {
        return "Audio processor that return audio library size.";
    }

    @Override
    public Entry<String, Number> evaluate(AudioLibrary lib) {
        int count = lib.getTotalEntriesCount();
        return new AbstractMap.SimpleEntry<String, Number>(lib.getId(), count);
    }

    @Override
    public double aggregationValue() {
        return 0.001;
    }

    @Override
    public String metricName() {
        return "LibCount";
    }

}
