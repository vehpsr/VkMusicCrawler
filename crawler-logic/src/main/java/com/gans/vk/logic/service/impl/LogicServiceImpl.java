package com.gans.vk.logic.service.impl;

import static com.gans.vk.context.SystemProperties.NumericProperty.CRAWLER_AUDIO_TOP_ARTISTS_COUNT;

import java.util.*;
import java.util.Map.Entry;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.data.*;
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
    public List<RecommendedArtistsData> recommendedWhiteListArtists(List<Entry<String, Double>> aggregatedData) {
        int artistCount = SystemProperties.get(CRAWLER_AUDIO_TOP_ARTISTS_COUNT);
        List<String> ids = new ArrayList<String>();
        for (Iterator<Entry<String, Double>> iterator = aggregatedData.iterator(); iterator.hasNext() && artistCount > 0; artistCount--) {
            ids.add(iterator.next().getKey());
        }
        return recommendedArtists(ids, getWhiteList());
    }

    @Override
    public List<RecommendedArtistsData> recommendedBlackListArtists(List<Entry<String, Double>> aggregatedData) {
        int artistCount = SystemProperties.get(CRAWLER_AUDIO_TOP_ARTISTS_COUNT);
        List<String> ids = new ArrayList<String>();
        for (ListIterator<Entry<String, Double>> iterator = aggregatedData.listIterator(aggregatedData.size()); iterator.hasPrevious() && artistCount > 0; artistCount--) {
            ids.add(iterator.previous().getKey());
        }
        return recommendedArtists(ids, getBlackList());
    }

    @Override
    public List<RecommendedArtistsData> recommendedBlackWithoutWhiteListArtists(List<Entry<String, Double>> aggregatedData) {
        int artistCount = SystemProperties.get(CRAWLER_AUDIO_TOP_ARTISTS_COUNT);
        List<String> ids = new ArrayList<String>();
        for (ListIterator<Entry<String, Double>> iterator = aggregatedData.listIterator(aggregatedData.size()); iterator.hasPrevious() && artistCount > 0; artistCount--) {
            ids.add(iterator.previous().getKey());
        }
        AudioLibrary lib = getBlackList();
        lib.putAll(getWhiteList().getEntries());
        return recommendedArtists(ids, lib);
    }

    public List<RecommendedArtistsData> recommendedArtists(List<String> ids, AudioLibrary list) {
        AudioLibrary artistCount = new AudioLibrary("artistCount");
        AudioLibrary songsCount = new AudioLibrary("songsCount");
        for (String id : ids) {
            AudioLibrary library = getLibrary(id);
            for (ArtistData data : library.getEntries()) {
                String artist = data.getKey();
                if (list.getCount(artist) == 0) {
                    artistCount.put(artist);
                    songsCount.put(data);
                }
            }
        }

        List<RecommendedArtistsData> recommendedArtists = new ArrayList<RecommendedArtistsData>();
        int recommendedArtistsCount = SystemProperties.get(CRAWLER_AUDIO_TOP_ARTISTS_COUNT);
        for (Iterator<ArtistData> iterator = artistCount.getEntries().iterator(); iterator.hasNext() && recommendedArtistsCount > 0; recommendedArtistsCount--) {
            ArtistData data = iterator.next();
            int totalSongs = songsCount.getCount(data.getKey());
            recommendedArtists.add(new RecommendedArtistsData(data.getKey(), data.getValue(), totalSongs));
        }

        return recommendedArtists;
    }
}
