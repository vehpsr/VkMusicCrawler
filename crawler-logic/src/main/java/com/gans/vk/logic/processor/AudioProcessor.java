package com.gans.vk.logic.processor;

import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;

public interface AudioProcessor {

    String metricDescription();

    String metricName();

    Entry<String, Number> evaluate(AudioLibrary lib);

    double aggregationValue();

}
