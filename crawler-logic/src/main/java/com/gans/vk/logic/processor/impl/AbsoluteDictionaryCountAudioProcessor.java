package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.CountMetric;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.processor.Dictionary;
import com.gans.vk.logic.processor.Dictionary.Lists;

public class AbsoluteDictionaryCountAudioProcessor extends AbstractDictionaryCountAudioProcessor {

    public AbsoluteDictionaryCountAudioProcessor(Dictionary dictionary, CountMode countMode, DictionaryList dictionaryList) {
        super(dictionary, countMode, dictionaryList);
    }

    @Override
    public String getDescription() {
        return MessageFormat.format("Absolute count of {0} entries in {1} list dictionary.", _countMode, _dictionaryList);
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        int count = getAbsoluteCount(lib);
        return new AbstractMap.SimpleEntry<String, Metric>(lib.getId(), new CountMetric(count));
    }

    public int getAbsoluteCount(AudioLibrary lib) {
        int count = 0;
        for (String artist : lib.getUniqueArtists()) {
            boolean isWhite = _dictionary.getCount(artist, Lists.WHITE) > 0;
            boolean isBlack = _dictionary.getCount(artist, Lists.BLACK) > 0;
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
}
