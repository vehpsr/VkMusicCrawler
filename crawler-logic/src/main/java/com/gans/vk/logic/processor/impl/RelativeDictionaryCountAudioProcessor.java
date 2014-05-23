package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.data.RatingMetric;
import com.gans.vk.logic.processor.AudioProcessor;

public class RelativeDictionaryCountAudioProcessor implements AudioProcessor {

    private AbsoluteDictionaryCountAudioProcessor _processor;

    public RelativeDictionaryCountAudioProcessor(AbsoluteDictionaryCountAudioProcessor processor) {
        _processor = processor;
    }

    @Override
    public String getDescription() {
        return MessageFormat.format("Ralative (absoluteCount/totalLibSize) count of {0} entries in {1} list dictionary.", _processor._countMode, _processor._dictionaryList);
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        int absoluteCount = _processor.getAbsoluteCount(lib);
        float relativeCount = 0.0f;

        switch (_processor._countMode) {
        case ARTISTS:
            relativeCount = (float) absoluteCount / lib.getUniqueEntriesCount();
            break;
        case SONGS:
            relativeCount = (float) absoluteCount / lib.getTotalEntriesCount();
            break;
        }

        return new AbstractMap.SimpleEntry<String, Metric>(lib.getId(), new RatingMetric(relativeCount));
    }

}
