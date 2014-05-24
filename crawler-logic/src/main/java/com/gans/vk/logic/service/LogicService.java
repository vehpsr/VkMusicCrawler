package com.gans.vk.logic.service;

import java.util.List;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;

public interface LogicService {

    AudioLibrary getWhiteList();

    AudioLibrary getBlackList();

    List<AudioProcessor> getProcessors();

    List<String> getAllAudioFiles();

    void save(List<String> statistics);

    AudioLibrary getLibrary(String file);

}
