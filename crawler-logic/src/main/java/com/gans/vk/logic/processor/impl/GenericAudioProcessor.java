package com.gans.vk.logic.processor.impl;

import com.gans.vk.logic.data.MonochromeList;
import com.gans.vk.logic.processor.AudioProcessor;

public abstract class GenericAudioProcessor implements AudioProcessor {

    protected MonochromeList _monochromeList;

    public GenericAudioProcessor(MonochromeList monochromeList) {
        _monochromeList = monochromeList;
    }
}
