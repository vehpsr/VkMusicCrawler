package com.gans.vk.audio.parser;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gans.vk.data.AudioLibrary;
import com.gans.vk.utils.TextUtils;

public class AudioParser {

    private static final Log LOG = LogFactory.getLog(AudioParser.class);

    @SuppressWarnings("unchecked")
    public static AudioLibrary parse(String json, String id) {
        if (StringUtils.isEmpty(json)) {
            return AudioLibrary.EMPTY;
        }
        final String ALL_SONGS_PROPERTY = "all";
        final int ARTIST_POSITION = 5;

        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject)parser.parse(json);
            JSONArray allSongs = (JSONArray)obj.get(ALL_SONGS_PROPERTY);
            AudioLibrary lib = new AudioLibrary(id);
            Iterator<JSONArray> songIterator = allSongs.iterator();
            while (songIterator.hasNext()) {
                JSONArray song = songIterator.next();
                List<String> artists = standardizeArtists((String)song.get(ARTIST_POSITION));
                for (String artist : artists) {
                    lib.put(artist);
                }
            }
            return lib;
        } catch (ParseException e) {
            LOG.error(MessageFormat.format("Fail to parse response: {0}\n{1}", e.getMessage(), TextUtils.shortVersion(json)));
        }
        return AudioLibrary.EMPTY;
    }

    private static List<String> standardizeArtists(String artist) {
        if (StringUtils.isEmpty(artist)) {
            return Collections.emptyList();
        }

        List<String> result = new LinkedList<String>();
        String[] artists = artist.toLowerCase().split("\\bfeat\\b");
        for (String rawName : artists) {
            String formatedName = rawName.replaceAll("\\bthe\\b", "").replaceAll("[^a-z0-9а-я]", "");
            if (StringUtils.isNotEmpty(formatedName)) {
                result.add(formatedName);
            }
        }
        return result;
    }
}
