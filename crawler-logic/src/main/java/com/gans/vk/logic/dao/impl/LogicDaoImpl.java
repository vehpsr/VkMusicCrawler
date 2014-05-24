package com.gans.vk.logic.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.processor.Dictionary;

public class LogicDaoImpl extends AbstractFileDao implements LogicDao {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("_yyyy_MM_dd_HH_mm");
    private static final String AUDIO_WHITELIST_DIR = SystemProperties.get(CRAWLER_AUDIO_WHITELIST_STASH);
    private static final String AUDIO_BLACKLIST_DIR = SystemProperties.get(CRAWLER_AUDIO_BLACKLIST_STASH);
    private static final String AUDIO_DATA_DIR = SystemProperties.get(CRAWLER_AUDIO_DATA_STASH);
    private static final String DEBUG_AUDIO_DATA_DIR = SystemProperties.get(CRAWLER_DEBUG_AUDIO_DATA_STASH);
    private static final String AUDIO_OUTPUT_DIR = SystemProperties.get(CRAWLER_AUDIO_OUTPUT_DIR);
    private static LogicDao _instance = new LogicDaoImpl();

    private boolean _debug = SystemProperties.debug();
    private String _audioDataDir;

    private LogicDaoImpl() {
        _audioDataDir = _debug ? DEBUG_AUDIO_DATA_DIR : AUDIO_DATA_DIR;
    }

    public static LogicDao getInstance() {
        return _instance;
    }

    @Override
    public AudioLibrary getWhiteList() {
        List<ArtistData> artistData = readEntriesFromDirectory(AUDIO_WHITELIST_DIR);
        AudioLibrary lib = new AudioLibrary(Dictionary.Lists.WHITE.toString());
        lib.putAll(artistData);
        return lib;
    }

    @Override
    public AudioLibrary getBlackList() {
        List<ArtistData> artistData = readEntriesFromDirectory(AUDIO_BLACKLIST_DIR);
        AudioLibrary lib = new AudioLibrary(Dictionary.Lists.BLACK.toString());
        lib.putAll(artistData);
        return lib;
    }

    private List<ArtistData> readEntriesFromDirectory(String dir) {
        List<String> entries = readFiles(dir);
        return ArtistData.convert(entries);
    }

    @Override
    public List<String> getAllAudioFiles() {
        return getAllFileNamesInDirectory(_audioDataDir);
    }

    @Override
    public AudioLibrary getLibrary(String fileName) {
        List<String> entries = readFileFromDirectory(_audioDataDir, fileName);
        AudioLibrary lib = new AudioLibrary(fileName);
        List<ArtistData> artistData = ArtistData.convert(entries);
        lib.putAll(artistData);
        return lib;
    }

    @Override
    public void save(List<String> statistics) {
        String fileName = "out" + DATE_FORMAT.format(new Date());
        save(AUDIO_OUTPUT_DIR, fileName, statistics);
    }
}
