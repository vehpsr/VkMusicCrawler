package com.gans.vk.logic.processor.impl;

import com.gans.vk.logic.data.MonochromeList;

public class BayesianAudioProcessor extends GenericAudioProcessor {

    public BayesianAudioProcessor(MonochromeList monochromeList) {
        super(monochromeList);
    }

    @Override
    public String getDescription() {
        return "Processor that use Bayesian probability to filter audio library";
    }

}
