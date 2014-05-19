package com.gans.vk.logic.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.dao.impl.LogicDaoImpl;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.Dictionary;
import com.gans.vk.logic.processor.impl.*;
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

        List<AudioProcessor> processors = new ArrayList<AudioProcessor>() {{
            add(new AvgRatingAudioProcessor(dictionary));
            add(new AbsoluteDiversityAudioProcessor());
            add(new PartialDiversityAudioProcessor(5));
            add(new PartialDiversityAudioProcessor(10));
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

}
