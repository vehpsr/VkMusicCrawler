package com.gans.vk.logic.processor.impl;

import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.service.BayesianService;

public abstract class GenericAudioProcessor implements AudioProcessor {

    protected BayesianService _bayesianService;

    public GenericAudioProcessor(BayesianService bayesianService) {
        _bayesianService = bayesianService;
    }
}
