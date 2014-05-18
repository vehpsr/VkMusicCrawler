package com.gans.vk.logic.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.*;

import java.util.*;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.service.BayesianService;

public class LogicDaoImpl extends AbstractFileDao implements LogicDao {

    private static final String AUDIO_WHITELIST_DIR = SystemProperties.get(CRAWLER_AUDIO_WHITELIST_STASH);
    private static final String AUDIO_BLACKLIST_DIR = SystemProperties.get(CRAWLER_AUDIO_BLACKLIST_STASH);
    private static final String AUDIO_DATA_DIR = SystemProperties.get(CRAWLER_AUDIO_DATA_STASH);
    private static final String DEBUG_AUDIO_DATA_DIR = SystemProperties.get(CRAWLER_DEBUG_AUDIO_DATA_STASH);
    private static LogicDao _instance = new LogicDaoImpl();

    private boolean _debug = SystemProperties.debug();

    private LogicDaoImpl() {
    }

    public static LogicDao getInstance() {
        return _instance;
    }

    @Override
    public AudioLibrary getWhiteList() {
        List<ArtistData> artistData = readEntriesFromDirectory(AUDIO_WHITELIST_DIR);
        AudioLibrary lib = new AudioLibrary(BayesianService.WHITE);
        lib.putAll(artistData);
        return lib;
    }

    @Override
    public AudioLibrary getBlackList() {
        List<ArtistData> artistData = readEntriesFromDirectory(AUDIO_BLACKLIST_DIR);
        AudioLibrary lib = new AudioLibrary(BayesianService.BLACK);
        lib.putAll(artistData);
        return lib;
    }

    private List<ArtistData> readEntriesFromDirectory(String dir) {
        List<String> entries = readFiles(dir);
        return ArtistData.convert(entries);
    }

    @Override
    public List<AudioLibrary> getAllAudio() {
        String audioDataDir = AUDIO_DATA_DIR;
        if (_debug) audioDataDir = DEBUG_AUDIO_DATA_DIR;

        Map<String, List<String>> allAudio = readAllFilesInDirectory(audioDataDir);
        List<AudioLibrary> result = new ArrayList<AudioLibrary>();
        for (String id : allAudio.keySet()) {
            AudioLibrary lib = new AudioLibrary(id);
            lib.putAll(ArtistData.convert(allAudio.get(id)));
            result.add(lib);
        }
        return result;
    }

}
