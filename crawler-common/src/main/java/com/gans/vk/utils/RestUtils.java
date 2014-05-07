package com.gans.vk.utils;

import static com.gans.vk.context.SystemProperties.NumericProperty.CRAWLER_DDOS_TIMEOUT_MIN;
import static com.gans.vk.context.SystemProperties.NumericProperty.CRAWLER_DDOS_TIMEOUT_RAND;

import org.apache.commons.lang3.StringUtils;

import com.gans.vk.context.SystemProperties;

public class RestUtils {

    public static void sleep() {
        try {
            Thread.sleep((long)(SystemProperties.get(CRAWLER_DDOS_TIMEOUT_MIN) + SystemProperties.get(CRAWLER_DDOS_TIMEOUT_RAND) * Math.random()));
        } catch (InterruptedException ignore) {}
    }

    public static void sleep(String cycles) {
        if (StringUtils.isEmpty(cycles)) {
            sleep();
            return;
        }
        String digits = cycles.replaceAll("\\D", "");
        if (StringUtils.isEmpty(digits)) {
            sleep();
            return;
        }
        int retryCount = Integer.parseInt(digits);
        for (int i = 0; i < retryCount; i++) {
            sleep();
        }
    }
}
