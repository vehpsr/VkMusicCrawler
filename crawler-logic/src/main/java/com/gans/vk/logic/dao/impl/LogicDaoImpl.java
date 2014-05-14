package com.gans.vk.logic.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_AUDIO_BLACKLIST_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_AUDIO_WHITELIST_STASH;

import java.util.List;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.data.MonochromeList;

public class LogicDaoImpl extends AbstractFileDao implements LogicDao {

    private static final String AUDIO_WHITELIST_DIR = SystemProperties.get(CRAWLER_AUDIO_WHITELIST_STASH);
    private static final String AUDIO_BLACKLIST_DIR = SystemProperties.get(CRAWLER_AUDIO_BLACKLIST_STASH);
    private static LogicDao _instance = new LogicDaoImpl();

    private LogicDaoImpl() {
    }

    public static LogicDao getInstance() {
        return _instance;
    }

    @Override
    public AudioLibrary getWhiteList() {
        List<ArtistData> artistData = readEntriesFromDirectory(AUDIO_WHITELIST_DIR);
        AudioLibrary lib = new AudioLibrary(MonochromeList.WHITE);
        lib.putAll(artistData);
        return lib;
    }

    @Override
    public AudioLibrary getBlackList() {
        List<ArtistData> artistData = readEntriesFromDirectory(AUDIO_BLACKLIST_DIR);
        AudioLibrary lib = new AudioLibrary(MonochromeList.BLACK);
        lib.putAll(artistData);
        return lib;
    }

    private List<ArtistData> readEntriesFromDirectory(String dir) {
        List<String> entries = readFiles(dir);
        return ArtistData.convert(entries);
    }

}
