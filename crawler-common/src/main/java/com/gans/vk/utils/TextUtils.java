package com.gans.vk.utils;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

public class TextUtils {

    public static String shortVersion(String st) {
        if (StringUtils.isEmpty(st)) {
            return "";
        }

        final int OUTPUT_THRESHOLD = 1000;
        if (st.length() > OUTPUT_THRESHOLD) {
            return MessageFormat.format("{0}\t...\t{1}", st.substring(0, OUTPUT_THRESHOLD / 2), st.substring(OUTPUT_THRESHOLD / 2));
        }
        return st;
    }
}
