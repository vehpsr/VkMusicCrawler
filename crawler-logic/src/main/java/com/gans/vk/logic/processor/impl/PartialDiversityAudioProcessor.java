package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.DiversityMetric;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.processor.AudioProcessor;

public class PartialDiversityAudioProcessor implements AudioProcessor {

    private int _partialDiversityPercentage;

    public PartialDiversityAudioProcessor(int partialDiversityPercentage) {
        if (partialDiversityPercentage > 100) {
            _partialDiversityPercentage = 100;
        } else if (partialDiversityPercentage < 0) {
            _partialDiversityPercentage = 0;
        } else {
            _partialDiversityPercentage = partialDiversityPercentage;
        }
    }

    @Override
    public String getDescription() {
        return MessageFormat.format("Partial diversity processor: contribution to audio collection by {0}% of top library atrists.", _partialDiversityPercentage);
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        int topPartialArtistsSubset = Math.round(lib.getUniqueEntriesCount() * partialPercentage());
        int topPartialArtistsSubsetCount = 0;
        List<ArtistData> artists = lib.getEntries();
        for (int i = 0; i < topPartialArtistsSubset; i++) {
            topPartialArtistsSubsetCount += artists.get(i).getValue();
        }
        float partialDiversity = (float) topPartialArtistsSubsetCount / lib.getTotalEntriesCount();
        return new AbstractMap.SimpleEntry<String, Metric>(lib.getId(), new DiversityMetric(partialDiversity));
    }

    private float partialPercentage() {
        return (float) _partialDiversityPercentage / 100;
    }

}
