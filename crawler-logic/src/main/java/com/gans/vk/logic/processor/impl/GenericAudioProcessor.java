package com.gans.vk.logic.processor.impl;

import java.util.List;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;

public abstract class GenericAudioProcessor implements AudioProcessor {

    @Override
    public DiversityStatistics getDiversityStatistics(AudioLibrary lib) {
        DiversityStatistics diversity = new DiversityStatistics();

        int totalEntriesCount = lib.getTotalEntriesCount();

        float absoluteDiversity = (float) lib.getUniqueEntriesCount() / totalEntriesCount;
        diversity.setAbsoluteDiversity(absoluteDiversity);

        int topArtistsSubset = Math.round(lib.getUniqueEntriesCount() * DiversityStatistics.PARTIAL_DIVERSITY_PERCENTAGE);
        int topArtistsCount = 0;
        List<ArtistData> artists = lib.getEntries();
        for (int i = 0; i < topArtistsSubset; i++) {
            topArtistsCount += artists.get(i).getValue();
        }
        float partialDiversity = (float) topArtistsCount / totalEntriesCount;
        diversity.setPartialDiversity(partialDiversity);

        return diversity;
    }

}
