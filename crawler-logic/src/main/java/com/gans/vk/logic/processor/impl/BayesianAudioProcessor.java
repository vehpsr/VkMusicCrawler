package com.gans.vk.logic.processor.impl;

import java.util.Map;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.data.MonochromeList;

public class BayesianAudioProcessor extends GenericAudioProcessor {

    public BayesianAudioProcessor(MonochromeList monochromeList) {
        super(monochromeList);
    }

    @Override
    public String getDescription() {
        return "Processor that use Bayesian probability to filter audio library";
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        Map<String, Float> dictionary = _monochromeList.dictionary();

        return null;
    }
}
