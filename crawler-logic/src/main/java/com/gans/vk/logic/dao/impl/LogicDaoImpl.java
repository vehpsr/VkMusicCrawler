package com.gans.vk.logic.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_AUDIO_BLACKLIST_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_AUDIO_WHITELIST_STASH;

import java.util.List;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;

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
    public List<ArtistData> getWhiteList() {
        return readEntriesFromDirectory(AUDIO_WHITELIST_DIR);
    }

    @Override
    public List<ArtistData> getBlackList() {
        return readEntriesFromDirectory(AUDIO_BLACKLIST_DIR);
    }

    private List<ArtistData> readEntriesFromDirectory(String dir) {
        List<String> entries = readFiles(dir);
        List<ArtistData> artistData = ArtistData.convert(entries);
        AudioLibrary lib = new AudioLibrary();
        lib.putAll(artistData);
        return lib.getEntries();
    }

}
