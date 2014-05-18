package com.gans.vk.logic.service;

import java.util.*;
import java.util.regex.Pattern;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;

public class BayesianService {
    public static final String BLACK = "BlackList";
    public static final String WHITE = "WhiteList";
    public static final float SPAMICITY_MIN = 0.01f;
    public static final float SPAMICITY_MAX = 0.99f;
    public static final float SPAMICITY_NEUTRAL = 0.5f;

    private final AudioLibrary _whiteList;
    private final AudioLibrary _blackList;

    private Map<String, Float> _dictionary = null;

    private BayesianService(MonochromeList builder) {
        _whiteList = builder._whiteList;
        _blackList = builder._blackList;

        _dictionary = dictionary();
    }

    /**
     * @return Map[Artist, SpamicityCoeficient] lookup dictionary
     */
    private Map<String, Float> dictionary() {
        if (_dictionary != null) {
            return _dictionary;
        }

        Set<String> artists = new HashSet<String>();
        artists.addAll(_whiteList.getUniqueArtists());
        artists.addAll(_blackList.getUniqueArtists());

        int totalWhiteCount = _whiteList.getTotalEntriesCount();
        int totalBlackCount = _blackList.getTotalEntriesCount();

        Map<String, Float> dictionary = new HashMap<String, Float>();
        for (String artist : artists) {
            int whiteCount = _whiteList.getCount(artist);
            int blackCount = _blackList.getCount(artist);
            if (whiteCount + blackCount == 0) {
                continue;
            }
            float whiteOccurrenceRate = (float) whiteCount / totalWhiteCount;
            float blackOccurrenceRate = (float) blackCount / totalBlackCount;
            float spamicity = blackOccurrenceRate / (blackOccurrenceRate + whiteOccurrenceRate);
            if (spamicity > SPAMICITY_MAX) {
                spamicity = SPAMICITY_MAX;
            } else if (spamicity < SPAMICITY_MIN) {
                spamicity = SPAMICITY_MIN;
            }
            dictionary.put(artist, spamicity);
        }

        return _dictionary = Collections.unmodifiableMap(dictionary);
    }

    public double spamicity(ArtistData data) {
        String artist = data.getKey();
        Float spamicity = _dictionary.get(artist);
        if (spamicity == null) {
            if (artist.startsWith("dj")) {
                spamicity = 0.6f;
            } else if (hasCyrillic(artist)) {
                spamicity = 0.6f;
            } else {
                spamicity = 0.4f;
            }
        }
        return spamicity;
    }

    private boolean hasCyrillic(String artist) {
        return Pattern.compile("[а-я]").matcher(artist).find();
    }

    public static class MonochromeList {
        private AudioLibrary _whiteList;
        private AudioLibrary _blackList;

        public MonochromeList white(AudioLibrary whiteList) {
            _whiteList = whiteList;
            return this;
        }

        public MonochromeList black(AudioLibrary blackList) {
            _blackList = blackList;
            return this;
        }

        public BayesianService train() {
            return new BayesianService(this);
        }
    }
}
