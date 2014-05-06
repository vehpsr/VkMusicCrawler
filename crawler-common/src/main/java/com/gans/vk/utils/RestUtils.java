package com.gans.vk.utils;

import static com.gans.vk.context.SystemProperties.NumericProperty.CRAWLER_DDOS_TIMEOUT;

import com.gans.vk.context.SystemProperties;

public class RestUtils {

    public static void sleep() {
        try {
            Thread.sleep((long)(1000 + SystemProperties.get(CRAWLER_DDOS_TIMEOUT) * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
