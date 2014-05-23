package com.gans.vk.logic.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.dao.impl.LogicDaoImpl;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.Dictionary;
import com.gans.vk.logic.processor.impl.*;
import com.gans.vk.logic.processor.impl.AbstractDictionaryCountAudioProcessor.CountMode;
import com.gans.vk.logic.processor.impl.AbstractDictionaryCountAudioProcessor.DictionaryList;
import com.gans.vk.logic.service.LogicService;

public class LogicServiceImpl implements LogicService {

    private static final Log LOG = LogFactory.getLog(LogicServiceImpl.class);
    private static LogicService _instance = new LogicServiceImpl();
    private LogicDao _logicDao;

    private LogicServiceImpl() {
        _logicDao = LogicDaoImpl.getInstance();
    }

    public static LogicService getInstance() {
        return _instance;
    }

    @Override
    public AudioLibrary getWhiteList() {
        return _logicDao.getWhiteList();
    }

    @Override
    public AudioLibrary getBlackList() {
        return _logicDao.getBlackList();
    }

    @Override
    @SuppressWarnings("serial")
    public List<AudioProcessor> getProcessors() {
        final Dictionary dictionary = getDictionary();

        final List<AbsoluteDictionaryCountAudioProcessor> absoluteCountProcessors = new LinkedList<AbsoluteDictionaryCountAudioProcessor>(){{
            add(new AbsoluteDictionaryCountAudioProcessor(dictionary, CountMode.ARTISTS, DictionaryList.WHITE_ONLY));
            add(new AbsoluteDictionaryCountAudioProcessor(dictionary, CountMode.ARTISTS, DictionaryList.WHITE_AND_BLACK));
            add(new AbsoluteDictionaryCountAudioProcessor(dictionary, CountMode.SONGS, DictionaryList.WHITE_ONLY));
            add(new AbsoluteDictionaryCountAudioProcessor(dictionary, CountMode.SONGS, DictionaryList.WHITE_AND_BLACK));
        }};

        final List<RelativeDictionaryCountAudioProcessor> relativeCountProcessors = new LinkedList<RelativeDictionaryCountAudioProcessor>();
        for (AbsoluteDictionaryCountAudioProcessor absoluteCountProcessor : absoluteCountProcessors) {
            relativeCountProcessors.add(new RelativeDictionaryCountAudioProcessor(absoluteCountProcessor));
        }

        List<AudioProcessor> processors = new LinkedList<AudioProcessor>() {{
            add(new AvgRatingAudioProcessor(dictionary));
            add(new AbsoluteDiversityAudioProcessor());
            add(new PartialDiversityAudioProcessor(5));
            add(new PartialDiversityAudioProcessor(10));
            addAll(absoluteCountProcessors);
            addAll(relativeCountProcessors);
        }};

        return processors;
    }

    private Dictionary getDictionary() {
        return new Dictionary.Builder()
                .white(getWhiteList())
                .black(getBlackList())
                .train();
    }

    @Override
    public List<AudioLibrary> getAllAudio() {
        return _logicDao.getAllAudio();
    }

    @Override
    public void save(List<String> statistics) {
        _logicDao.save(statistics);
    }

}
