package com.gans.vk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class HtmlUtils {

    private static final String EMPTY_HTML = "<div />";
    private static final Pattern HTML_COMPONENT_PATTERN = Pattern.compile("<\\s*(html|body|div)[^>]*>.*<\\s*/\\s*\\1\\s*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static String sanitizeHtml(String html) {
        if (StringUtils.isBlank(html)) {
            return EMPTY_HTML;
        }
        Matcher matcher = HTML_COMPONENT_PATTERN.matcher(html);
        if (matcher.find()) {
            return html.substring(matcher.start(), matcher.end());
        }
        return EMPTY_HTML;
    }
}
