package com.gans.vk.logic.processor.impl;

import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.data.MonochromeList;
import com.gans.vk.logic.processor.AudioProcessor;

public abstract class GenericAudioProcessor implements AudioProcessor {

    protected MonochromeList _monochromeList;

    public GenericAudioProcessor(MonochromeList monochromeList) {
        _monochromeList = monochromeList;
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        return null;
    }
}
