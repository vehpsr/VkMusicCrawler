package com.gans.vk.logic.dao;


import java.util.List;

import com.gans.vk.data.AudioLibrary;

public interface LogicDao {

    AudioLibrary getWhiteList();

    AudioLibrary getBlackList();

    List<AudioLibrary> getAllAudio();

}
