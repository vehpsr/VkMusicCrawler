package com.gans.vk.logic.processor.impl;

import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.Dictionary;

public abstract class AbstractDictionaryCountAudioProcessor implements AudioProcessor {

    public enum CountMode {
        ARTISTS,
        SONGS
    }

    public enum DictionaryList {
        WHITE_ONLY,
        WHITE_AND_BLACK
    }

    protected Dictionary _dictionary;
    protected CountMode _countMode;
    protected DictionaryList _dictionaryList;

    public AbstractDictionaryCountAudioProcessor(Dictionary dictionary, CountMode countMode, DictionaryList dictionaryList) {
        _dictionary = dictionary;
        _countMode = countMode;
        _dictionaryList = dictionaryList;
    }
}
