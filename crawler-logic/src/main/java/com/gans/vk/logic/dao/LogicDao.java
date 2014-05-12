package com.gans.vk.logic.dao;


import com.gans.vk.data.AudioLibrary;

public interface LogicDao {

    AudioLibrary getWhiteList();

    AudioLibrary getBlackList();

}
