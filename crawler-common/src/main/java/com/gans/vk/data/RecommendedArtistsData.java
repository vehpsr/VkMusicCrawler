package com.gans.vk.data;

import java.text.MessageFormat;

public class RecommendedArtistsData {

    private String _artist;
    private int _occurences;
    private int _totalSongs;

    public RecommendedArtistsData(String artist, int occurences, int totalSongs) {
        _artist = artist;
        _occurences = occurences;
        _totalSongs = totalSongs;
    }

    public String format() {
        return MessageFormat.format("{0}\t{1}\t{2}", _artist, _occurences, _totalSongs);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {1} {2}", _artist, _occurences, _totalSongs);
    }
}
