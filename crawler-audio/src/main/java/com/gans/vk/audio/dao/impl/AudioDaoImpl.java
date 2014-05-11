package com.gans.vk.audio.dao.impl;

import static com.gans.vk.context.SystemProperties.Property.CRAWLER_AUDIO_DATA_STASH;
import static com.gans.vk.context.SystemProperties.Property.CRAWLER_ID_STASH;

import java.util.LinkedList;
import java.util.List;

import com.gans.vk.audio.dao.AudioDao;
import com.gans.vk.context.SystemProperties;
import com.gans.vk.dao.AbstractFileDao;
import com.gans.vk.data.ArtistData;

public class AudioDaoImpl extends AbstractFileDao implements AudioDao {

    private static final String ID_STORAGE_PATH = SystemProperties.get(CRAWLER_ID_STASH);
    private static final String AUDIO_STORAGE_DIR = SystemProperties.get(CRAWLER_AUDIO_DATA_STASH);
    private static AudioDao _instance = new AudioDaoImpl();

    private AudioDaoImpl() {
    }

    public static AudioDao getInstance() {
        return _instance;
    }

    @Override
    public List<String> getAllIdsFromStash() {
        return readFile(ID_STORAGE_PATH, ReadMode.UNIQUE);
    }

    @Override
    public List<String> getAlreadyProcessedIds() {
        return getAllFileNamesInDirectory(AUDIO_STORAGE_DIR);
    }

    @Override
    public void saveAudioCollection(String id, List<ArtistData> entries) {
        List<String> data = new LinkedList<String>();
        for (ArtistData entry : entries) {
            data.add(entry.format());
        }
        save(AUDIO_STORAGE_DIR, id, data);
    }

}
