package com.gans.vk.logic.service.impl;

import java.util.*;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.dao.LogicDao;
import com.gans.vk.logic.dao.impl.LogicDaoImpl;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.Dictionary;
import com.gans.vk.logic.processor.impl.*;
import com.gans.vk.logic.processor.impl.AbstractDictionaryCountAudioProcessor.CountMode;
import com.gans.vk.logic.processor.impl.AbstractDictionaryCountAudioProcessor.DictionaryList;
import com.gans.vk.logic.service.LogicService;
import com.google.common.collect.Multimap;

public class LogicServiceImpl implements LogicService {

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
            add(new LibraryCountAudioProcessor());
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
    public List<String> getAllAudioFiles() {
        return _logicDao.getAllAudioFiles();
    }

    @Override
    public AudioLibrary getLibrary(String file) {
        return _logicDao.getLibrary(file);
    }

    @Override
    public void save(List<String> statistics) {
        _logicDao.save(statistics);
    }

    @Override
    public List<Entry<String, Double>> getAggregatedMetricData(Multimap<String, Entry<AudioProcessor, Number>> metrics) {
        List<Entry<String, Double>> aggregatedMetric = new ArrayList<Entry<String, Double>>();
        for (String id : metrics.keySet()) {
            double metric = 0;
            Collection<Entry<AudioProcessor, Number>> entries = metrics.get(id);
            for (Entry<AudioProcessor, Number> entry : entries) {
                metric += (entry.getValue().doubleValue() * entry.getKey().aggregationValue());
            }
            aggregatedMetric.add(new AbstractMap.SimpleEntry<String, Double>(id, metric));
        }
        Collections.sort(aggregatedMetric, new Comparator<Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> v1, Entry<String, Double> v2) {
                return v2.getValue().compareTo(v1.getValue());
            }
        });
        return aggregatedMetric;
    }

    @Override
    public Map<String, Entry<Integer, Integer>> computeRecommendedArtists(List<Entry<String, Double>> aggregatedData) {
        // TODO Auto-generated method stub
        return null;
    }
}
