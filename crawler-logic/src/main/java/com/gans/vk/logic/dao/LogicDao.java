package com.gans.vk.logic.dao;

import java.util.List;

import com.gans.vk.data.ArtistData;

public interface LogicDao {

    List<ArtistData> getWhiteList();

    List<ArtistData> getBlackList();

}
