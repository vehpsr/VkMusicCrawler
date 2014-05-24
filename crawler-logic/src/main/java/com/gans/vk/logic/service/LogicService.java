package com.gans.vk.logic.service;

import java.util.List;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;
import com.google.common.collect.Multimap;

public interface LogicService {

    AudioLibrary getWhiteList();

    AudioLibrary getBlackList();

    List<AudioProcessor> getProcessors();

    List<String> getAllAudioFiles();

    void save(List<String> statistics);

    AudioLibrary getLibrary(String file);

    List<Entry<String, Double>> getAggregatedMetricData(Multimap<String, Entry<AudioProcessor, Number>> metrics);

}
