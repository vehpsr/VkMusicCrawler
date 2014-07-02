package com.gans.vk.context;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class SystemProperties {

    private final static String PROPERTY_FILE_NAME = "app.properties";
    private static Properties _properties = loadProperties();

    public enum Property {
        VK_PASS("vk.pass"),
        VK_LOGIN("vk.login"),
        VK_ID("vk.id"),
        VK_DOMAIN("vk.domain"),
        VK_AUTH_LOGIN_URL_PATTERN("vk.auth.loginUrlPattern"),
        VK_AUTH_COOKIE_DOMAIN("vk.auth.cookieDomain"),
        VK_AUDIO_URL("vk.audio.url"),
        VK_AUDIO_ENTITY_PATTERN("vk.audio.entityPattern"),
        VK_GROUP_MEMBERS_URL("vk.groupMembers.url"),
        VK_GROUP_MEMBERS_ENTITY_PATTERN("vk.groupMembers.entityPattern"),

        VK_HEADER_CONTENT_TYPE("vk.header.content-type"),
        VK_HEADER_USER_AGENT("vk.header.user-agent"),

        CRAWLER_DEBUG("crawler.debug"),

        CRAWLER_ID_STASH("crawler.id.stash"),
        CRAWLER_GROUP_STASH("crawler.group.stash"),
        CRAWLER_URL_STASH("crawler.url.stash"),
        CRAWLER_AUDIO_DATA_STASH("crawler.audio.data.stash"),
        CRAWLER_AUDIO_WHITELIST_STASH("crawler.audio.whitelist.stash"),
        CRAWLER_AUDIO_BLACKLIST_STASH("crawler.audio.blacklist.stash"),
        CRAWLER_AUDIO_OUTPUT_DIR("crawler.audio.output.dir"),

        CRAWLER_DEBUG_AUDIO_DATA_STASH("crawler.debug.audio.data.stash");

        private String key;

        Property(String key) {
            this.key = key;
        }
    }

    public enum NumericProperty {
        CRAWLER_DDOS_TIMEOUT_MIN("crawler.ddosTimeout.min"),
        CRAWLER_DDOS_TIMEOUT_RAND("crawler.ddosTimeout.randomization"),
        CRAWLER_ID_MIN_AUDIO_LIB_SIZE("crawler.id.minAudioLibrarySize"),
        CRAWLER_AUDIO_TOP_ARTISTS_COUNT("crawler.audio.report.topArtistsCount");

        private String key;

        NumericProperty(String key) {
            this.key = key;
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        InputStream is = ClassLoader.getSystemResourceAsStream(PROPERTY_FILE_NAME);
        if (is == null) {
            throw new AssertionError(MessageFormat.format("Fail to read property file {0}: does not exists or invalid", PROPERTY_FILE_NAME));
        }
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    public static String get(Property prop) {
        return _properties.getProperty(prop.key);
    }

    public static String get(Property prop, String defaultValue) {
        return _properties.getProperty(prop.key, defaultValue);
    }

    public static int get(NumericProperty prop) {
        return Integer.parseInt(_properties.getProperty(prop.key));
    }

    public static int get(NumericProperty prop, int defaultValue) {
        return Integer.parseInt(_properties.getProperty(prop.key, String.valueOf(defaultValue)));
    }

    public static boolean debug() {
        String debug = get(Property.CRAWLER_DEBUG, "false");
        return new Boolean(debug);
    }
}
