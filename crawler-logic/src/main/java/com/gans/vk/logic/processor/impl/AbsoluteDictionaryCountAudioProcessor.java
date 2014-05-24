package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.Dictionary;

public class AbsoluteDictionaryCountAudioProcessor extends AbstractDictionaryCountAudioProcessor {

    public AbsoluteDictionaryCountAudioProcessor(Dictionary dictionary, CountMode countMode, DictionaryList dictionaryList) {
        super(dictionary, countMode, dictionaryList);
    }

    @Override
    public String metricDescription() {
        return MessageFormat.format("Absolute count of {0} entries in {1} list dictionary.", _countMode, _dictionaryList);
    }

    @Override
    public Entry<String, Number> evaluate(AudioLibrary lib) {
        int count = getAbsoluteCount(lib);
        return new AbstractMap.SimpleEntry<String, Number>(lib.getId(), count);
    }

    public int getAbsoluteCount(AudioLibrary lib) {
        int count = 0;
        for (String artist : lib.getUniqueArtists()) {
            boolean isWhite = _dictionary.isWhite(artist);
            boolean isBlack = _dictionary.isBlack(artist);
            if (!isWhite && !isBlack) {
                continue;
            }

            int songs = lib.getCount(artist);

            switch (_dictionaryList) {
            case WHITE_ONLY:
                switch (_countMode) {
                case ARTISTS:
                    if (isWhite) count++;
                    break;
                case SONGS:
                    if (isWhite) count += songs;
                    break;
                }
                break;
            case WHITE_AND_BLACK:
                switch (_countMode) {
                case ARTISTS:
                    if (isWhite) count++;
                    if (isBlack) count--;
                    break;
                case SONGS:
                    if (isWhite) count += songs;
                    if (isBlack) count -= songs;
                    break;
                }
                break;
            }
        }
        return count;
    }

    @Override
    public double aggregationValue() {
        if (_dictionaryList.equals(DictionaryList.WHITE_AND_BLACK) && _countMode.equals(CountMode.SONGS)) {
            return 0.006;
        } else {
            return 0;
        }
    }

    @Override
    public String metricName() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbsDicCount_");
        if (_dictionaryList.equals(DictionaryList.WHITE_ONLY)) {
            builder.append("W_");
        } else if (_dictionaryList.equals(DictionaryList.WHITE_AND_BLACK)) {
            builder.append("WB_");
        }
        if (_countMode.equals(CountMode.ARTISTS)) {
            builder.append("A");
        } else if (_countMode.equals(CountMode.SONGS)) {
            builder.append("S");
        }
        return builder.toString();
    }
}
