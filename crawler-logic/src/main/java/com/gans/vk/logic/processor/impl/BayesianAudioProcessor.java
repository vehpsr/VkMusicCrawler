package com.gans.vk.logic.processor.impl;

public class BayesianAudioProcessor extends GenericAudioProcessor {

    @Override
    public String getProcessorDescription() {
        return "Processor that use Bayesian probability to filter audio library";
    }

}
