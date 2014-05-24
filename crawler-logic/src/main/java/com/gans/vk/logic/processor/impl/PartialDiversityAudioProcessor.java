package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
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
    public String metricDescription() {
        return MessageFormat.format("Partial diversity processor: contribution to audio collection by {0}% of top library atrists.", _partialDiversityPercentage);
    }

    @Override
    public Entry<String, Number> evaluate(AudioLibrary lib) {
        int topPartialArtistsSubset = Math.round(lib.getUniqueEntriesCount() * partialPercentage());
        int topPartialArtistsSubsetCount = 0;
        List<ArtistData> artists = lib.getEntries();
        for (int i = 0; i < topPartialArtistsSubset; i++) {
            topPartialArtistsSubsetCount += artists.get(i).getValue();
        }
        float partialDiversity = (float) topPartialArtistsSubsetCount / lib.getTotalEntriesCount() * 100;
        return new AbstractMap.SimpleEntry<String, Number>(lib.getId(), partialDiversity);
    }

    private float partialPercentage() {
        return (float) _partialDiversityPercentage / 100;
    }

    @Override
    public double aggregationValue() {
        return 0;
    }

    @Override
    public String metricName() {
        return "PartDiv-ty_" + _partialDiversityPercentage;
    }

}
