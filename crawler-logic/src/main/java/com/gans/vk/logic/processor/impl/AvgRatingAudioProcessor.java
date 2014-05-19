package com.gans.vk.logic.processor.impl;

import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.data.RatingMetric;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.Dictionary;

public class AvgRatingAudioProcessor implements AudioProcessor {

    protected Dictionary _dictionary;

    public AvgRatingAudioProcessor(Dictionary dictionary) {
        _dictionary = dictionary;
    }

    @Override
    public String getDescription() {
        return "Processor that return average audio library rating";
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        double rating = 0;
        for (ArtistData data : lib.getEntries()) {
            double artistRating = _dictionary.rating(data);
            rating += artistRating;
        }

        float result = (float) (rating / lib.getUniqueEntriesCount());
        return new AbstractMap.SimpleEntry<String, Metric>(lib.getId(), new RatingMetric(result));
    }
}
