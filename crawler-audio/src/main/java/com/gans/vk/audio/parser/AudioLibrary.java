package com.gans.vk.audio.parser;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

public class AudioLibrary {

    private static final String SEPARATOR = "\t";
    private final Map<String, Integer> _artistsCount = new HashMap<String, Integer>();

    public void put(String artist) {
        Integer count = _artistsCount.get(artist);
        if (count == null) {
            count = 0;
        }
        count++;
        _artistsCount.put(artist, count);
    }

    public boolean isEmpty() {
        return _artistsCount.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public List<ArtistData> getEntries() {
        List<ArtistData> result = new ArrayList<ArtistData>();
        for (Entry<String, Integer> entry : _artistsCount.entrySet()) {
            result.add(new ArtistData(entry));
        }
        Collections.sort(result);
        return result;
    }

    public static class ArtistData implements Entry<String, Integer>, Comparable<ArtistData> {

        private String _key;
        private Integer _value;

        public ArtistData(String key, Integer value) {
            _key = key;
            _value = value;
        }

        public ArtistData(Entry<String, Integer> entry) {
            this(entry.getKey(), entry.getValue());
        }

        @Override
        public String getKey() {
            return _key;
        }

        @Override
        public Integer getValue() {
            return _value;
        }

        @Override
        public Integer setValue(Integer value) {
            return _value = value;
        }

        @Override
        public int compareTo(ArtistData other) {
            if (this._value > other._value) {
                return -1;
            } else if (this._value == other._value) {
                return this._key.compareTo(other._key);
            } else {
                return 1;
            }
        }

        public String format() {
            return MessageFormat.format("{0}{1}{2,number,#}", _key, SEPARATOR, _value);
        }

        @Override
        public String toString() {
            return format();
        }
    }
}
