package com.gans.vk.logic.processor;

import java.util.*;
import java.util.regex.Pattern;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;

public class Dictionary {

    public enum Lists {
        BLACK,
        WHITE
    }

    private final AudioLibrary _whiteList;
    private final AudioLibrary _blackList;

    /** Map[Artist, Rating] lookup dictionary */
    private Map<String, Float> _artistRating = null;

    private Dictionary(Builder builder) {
        _whiteList = builder._whiteList;
        _blackList = builder._blackList;

        _artistRating = initData();
    }

    private Map<String, Float> initData() {
        if (_artistRating != null) {
            return _artistRating;
        }
        final float MAX_BASE_RATING = 0.8f;
        final float MIN_BASE_RATING = 0.2f;

        Set<String> artists = new HashSet<String>();
        artists.addAll(_whiteList.getUniqueArtists());
        artists.addAll(_blackList.getUniqueArtists());

        int totalWhiteCount = _whiteList.getTotalEntriesCount();
        int totalBlackCount = _blackList.getTotalEntriesCount();

        Map<String, Float> data = new HashMap<String, Float>();
        for (String artist : artists) {
            int whiteCount = _whiteList.getCount(artist);
            int blackCount = _blackList.getCount(artist);
            if (whiteCount + blackCount == 0) {
                continue;
            }
            float whiteOccurrenceRate = (float) whiteCount / totalWhiteCount;
            float blackOccurrenceRate = (float) blackCount / totalBlackCount;
            float rating = whiteOccurrenceRate / (blackOccurrenceRate + whiteOccurrenceRate);
            if (rating > MAX_BASE_RATING) {
                rating = MAX_BASE_RATING;
                rating = (float) adjustOccurrence(rating, whiteCount - blackCount);
            } else if (rating < MIN_BASE_RATING) {
                rating = MIN_BASE_RATING;
                rating = (float) adjustOccurrence(rating, blackCount - whiteCount);
            }
            data.put(artist, rating);
        }

        return _artistRating = Collections.unmodifiableMap(data);
    }

    public double rating(ArtistData data) {
        String artist = data.getKey();
        Float baseRating = _artistRating.get(artist);
        if (baseRating == null) {
            if (artist.startsWith("dj")) {
                baseRating = 0.35f;
            } else if (hasCyrillic(artist)) {
                baseRating = 0.4f;
            } else {
                baseRating = 0.55f;
            }
        }

        int count = data.getValue();
        double rating = adjustOccurrence(baseRating, count);
        return rating;
    }

    private double adjustOccurrence(float baseRating, int count) {
        final int COUNT_LIMIT = 13;
        final float NEUTRAL_RATING = 0.5f;

        if (baseRating > NEUTRAL_RATING) {
            return baseRating * Math.pow(1.05, Math.min(count, COUNT_LIMIT));
        } else if (baseRating < NEUTRAL_RATING) {
            return baseRating * Math.pow(0.94, count);
        }
        return NEUTRAL_RATING;
    }

    private boolean hasCyrillic(String artist) {
        return Pattern.compile("[а-я]").matcher(artist).find();
    }

    public boolean isWhite(String artist) {
        return _whiteList.getCount(artist) > 0;
    }

    public boolean isBlack(String artist) {
        return _blackList.getCount(artist) > 0;
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

        public Dictionary train() {
            return new Dictionary(this);
        }
    }
}
