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
        VK_HOST("vk.host"),
        VK_ID("vk.id"),
        VK_COOKIES("vk.cookies"),
        VK_AUDIO_URL("vk.audioUrl");

        private String key;

        Property(String key) {
            this.key = key;
        }
    }

    public enum NumericProperty {
        VK_PORT("vk.port"),
        CRAWLER_DDOS_TIMEOUT("crawler.ddosTimeout");

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
}
