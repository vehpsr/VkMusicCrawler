package com.gans.vk.logic.data;

import com.gans.vk.data.AudioLibrary;

public class MonochromeList {
    public static final String BLACK = "BlackList";
    public static final String WHITE = "WhiteList";

    private final AudioLibrary _whiteList;
    private final AudioLibrary _blackList;

    public AudioLibrary getWhiteList() {
        return _whiteList;
    }

    public AudioLibrary getBlackList() {
        return _blackList;
    }

    public static class Builder {
        private AudioLibrary _whiteList;
        private AudioLibrary _blackList;

        public Builder white(AudioLibrary whiteList) {
            _whiteList = whiteList;
            return this;
        }

        public Builder black(AudioLibrary blackList) {
            _blackList = blackList;
            return this;
        }

        public MonochromeList build() {
            return new MonochromeList(this);
        }
    }

    private MonochromeList(Builder builder) {
        _whiteList = builder._whiteList;
        _blackList = builder._blackList;
    }
}
