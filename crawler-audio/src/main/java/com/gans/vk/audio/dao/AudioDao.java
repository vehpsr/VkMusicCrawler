package com.gans.vk.audio.dao;

import java.util.List;

import com.gans.vk.data.ArtistData;

public interface AudioDao {

    List<String> getAllIdsFromStash();

    List<String> getAlreadyProcessedIds();

    void saveAudioCollection(String id, List<ArtistData> entries);

}
