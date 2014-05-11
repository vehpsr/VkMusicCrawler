package com.gans.vk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class HtmlUtils {

    private static final String EMPTY_HTML = "<div />";
    private static final Pattern HTML_COMPONENT_PATTERN = Pattern.compile("<\\s*(html|body|div)[^>]*>.*<\\s*/\\s*\\1\\s*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern JSON_PATTERN = Pattern.compile("^[^{]*(\\{\\s*(\"|')\\w+\\2\\s*:.*\\})[^}]*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final String VK_JSON_SEPARATOR_PATTERN = "<\\s*!\\s*>";

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

    public static String[] sanitizeJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return new String[]{};
        }
        Matcher matcher = JSON_PATTERN.matcher(json);
        if (matcher.matches()) {
            return matcher.group(1).replaceAll("'", "\"").split(VK_JSON_SEPARATOR_PATTERN);
        }
        return new String[]{};
    }
}
