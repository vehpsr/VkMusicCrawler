package com.gans.vk.logic.processor.impl;

import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.Dictionary;

public class AvgRatingAudioProcessor implements AudioProcessor {

    protected Dictionary _dictionary;

    public AvgRatingAudioProcessor(Dictionary dictionary) {
        _dictionary = dictionary;
    }

    @Override
    public String metricDescription() {
        return "Processor that return average audio library rating";
    }

    @Override
    public Entry<String, Number> evaluate(AudioLibrary lib) {
        double rating = 0;
        for (ArtistData data : lib.getEntries()) {
            double artistRating = _dictionary.rating(data);
            rating += artistRating;
        }

        float avgRating = (float) (rating / lib.getUniqueEntriesCount()) * 100;
        return new AbstractMap.SimpleEntry<String, Number>(lib.getId(), avgRating);
    }

    @Override
    public double aggregationValue() {
        return 1;
    }

    @Override
    public String metricName() {
        return "AvgRating";
    }
}
