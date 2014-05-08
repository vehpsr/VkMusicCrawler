package com.gans.vk.audio.dao;

import java.util.List;

public interface AudioDao {

    List<String> getAllIdsFromStash();

    List<String> getAlreadyProcessedIds();

}
