package com.gans.vk.data;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArtistData  implements Entry<String, Integer>, Comparable<ArtistData> {

    private static final Log LOG = LogFactory.getLog(ArtistData.class);
    private static final String DATA_SEPARATOR = "\t";
    private static final Pattern DATA_SEPARATOR_PATTERN = Pattern.compile("^([^,\\s]+?)(?:,\\s*|\\s+)(\\d+)$");

    private String _key;
    private Integer _value;

    public ArtistData(String key, Integer value) {
        _key = key;
        _value = value;
    }

    public ArtistData(Entry<String, Integer> entry) {
        this(entry.getKey(), entry.getValue());
    }

    @Override
    public String getKey() {
        return _key;
    }

    @Override
    public Integer getValue() {
        return _value;
    }

    @Override
    public Integer setValue(Integer value) {
        return _value = value;
    }

    @Override
    public int compareTo(ArtistData other) {
        if (this._value > other._value) {
            return -1;
        } else if (this._value == other._value) {
            return this._key.compareTo(other._key);
        } else {
            return 1;
        }
    }

    public String format() {
        return MessageFormat.format("{0}{1}{2,number,#}", _key, DATA_SEPARATOR, _value);
    }

    @Override
    public String toString() {
        return format();
    }

    public static List<ArtistData> convert(List<String> formatedEntries) {
        List<ArtistData> data = new ArrayList<ArtistData>();
        for (String entry : formatedEntries) {
            Matcher matcher = DATA_SEPARATOR_PATTERN.matcher(entry);
            if (matcher.matches()) {
                String artist = matcher.group(1);
                int count = Integer.parseInt(matcher.group(2));
                data.add(new ArtistData(artist, count));
            } else {
                LOG.warn(MessageFormat.format("Fail to match formated entry {0} to pattern {1}", entry, DATA_SEPARATOR_PATTERN.toString()));
            }
        }
        return data;
    }
}
