package com.gans.vk.data;

import java.util.*;
import java.util.Map.Entry;


public class AudioLibrary {

    private final Map<String, Integer> _artistsCount = new HashMap<String, Integer>();

    public void put(String artist) {
        increment(artist, 1);
    }

    public void put(ArtistData entry) {
        increment(entry.getKey(), entry.getValue());
    }

    public void putAll(List<ArtistData> artistData) {
        for (ArtistData entry : artistData) {
            put(entry);
        }
    }

    private void increment(String artist, int amount) {
        Integer count = _artistsCount.get(artist);
        if (count == null) {
            count = 0;
        }
        count += amount;
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
}
