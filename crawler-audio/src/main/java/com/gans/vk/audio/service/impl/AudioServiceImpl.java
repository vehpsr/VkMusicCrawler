package com.gans.vk.audio.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.gans.vk.audio.dao.AudioDao;
import com.gans.vk.audio.dao.impl.AudioDaoImpl;
import com.gans.vk.audio.service.AudioService;

public class AudioServiceImpl implements AudioService {

    private static AudioService _instance = new AudioServiceImpl();
    private static AudioDao _audioDao;

    private AudioServiceImpl() {
        _audioDao = AudioDaoImpl.getInstance();
    }

    public static AudioService getInstance() {
        return _instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getIdsForAudioDiscovery() {
        List<String> stashIds = _audioDao.getAllIdsFromStash();
        List<String> alreadyProcessedIds = _audioDao.getAlreadyProcessedIds();
        return CollectionUtils.subtract(stashIds, alreadyProcessedIds);
    }

}
