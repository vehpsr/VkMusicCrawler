package com.gans.vk.logic.data;

import java.util.*;

import com.gans.vk.data.AudioLibrary;

public class MonochromeList {
    public static final String BLACK = "BlackList";
    public static final String WHITE = "WhiteList";

    private final AudioLibrary _whiteList;
    private final AudioLibrary _blackList;

    private Map<String, Float> _dictionary = null;

    private MonochromeList(Builder builder) {
        _whiteList = builder._whiteList;
        _blackList = builder._blackList;
    }

    /**
     * @return Map[Artist, SpamicityCoeficient] lookup dictionary
     */
    public Map<String, Float> dictionary() {
        if (_dictionary != null) {
            return _dictionary;
        }

        final float SPAMICITY_MIN = 0.01f;
        final float SPAMICITY_MAX = 0.99f;

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

    public static class Builder {
        private AudioLibrary _whiteList;
        private AudioLibrary _blackList;

        public Builder white(AudioLibrary whiteList) {
            _whiteList = whiteList;
            return this;
        }

        public Builder black(AudioLibrary blackList) {
            _blackList = blackList;
            return this;
        }

        public MonochromeList build() {
            return new MonochromeList(this);
        }
    }
}
