package com.gans.vk.logic.processor;

import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.Metric;

public interface AudioProcessor {

    String getDescription();

    Entry<String, Metric> evaluate(AudioLibrary lib);

}
