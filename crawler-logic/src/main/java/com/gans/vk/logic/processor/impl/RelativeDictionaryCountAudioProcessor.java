package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Map.Entry;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.processor.AudioProcessor;
import com.gans.vk.logic.processor.impl.AbstractDictionaryCountAudioProcessor.CountMode;
import com.gans.vk.logic.processor.impl.AbstractDictionaryCountAudioProcessor.DictionaryList;

public class RelativeDictionaryCountAudioProcessor implements AudioProcessor {

    private AbsoluteDictionaryCountAudioProcessor _processor;

    public RelativeDictionaryCountAudioProcessor(AbsoluteDictionaryCountAudioProcessor processor) {
        _processor = processor;
    }

    @Override
    public String metricDescription() {
        return MessageFormat.format("Ralative (absoluteCount/totalLibSize) count of {0} entries in {1} list dictionary.", _processor._countMode, _processor._dictionaryList);
    }

    @Override
    public Entry<String, Number> evaluate(AudioLibrary lib) {
        int absoluteCount = _processor.getAbsoluteCount(lib);
        float relativeCount = 0.0f;

        switch (_processor._countMode) {
        case ARTISTS:
            relativeCount = (float) absoluteCount / lib.getUniqueEntriesCount() * 100;
            break;
        case SONGS:
            relativeCount = (float) absoluteCount / lib.getTotalEntriesCount() * 100;
            break;
        }

        return new AbstractMap.SimpleEntry<String, Number>(lib.getId(), relativeCount);
    }

    @Override
    public double aggregationValue() {
        if (_processor._dictionaryList.equals(DictionaryList.WHITE_ONLY) && _processor._countMode.equals(CountMode.ARTISTS)) {
            return 0.75;
        } else {
            return 0;
        }
    }

    @Override
    public String metricName() {
        StringBuilder builder = new StringBuilder();
        builder.append("RelDicCount_");
        if (_processor._dictionaryList.equals(DictionaryList.WHITE_ONLY)) {
            builder.append("W_");
        } else if (_processor._dictionaryList.equals(DictionaryList.WHITE_AND_BLACK)) {
            builder.append("WB_");
        }
        if (_processor._countMode.equals(CountMode.ARTISTS)) {
            builder.append("A");
        } else if (_processor._countMode.equals(CountMode.SONGS)) {
            builder.append("S");
        }
        return builder.toString();
    }
}
