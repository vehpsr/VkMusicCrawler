package com.gans.vk.logic.service;

import java.util.List;

import com.gans.vk.data.ArtistData;

public interface LogicService {

    List<ArtistData> getWhiteList();

    List<ArtistData> getBlackList();

}
