package com.gans.vk.logic.data;

import java.text.DecimalFormat;

public class BayesianMetric implements Metric, Comparable<BayesianMetric> {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#.0000");
    private double _spamicityValue;

    public BayesianMetric(double spamicityValue) {
        _spamicityValue = spamicityValue;
    }

    @Override
    public String get() {
        return FORMATTER.format(_spamicityValue * 100);
    }

    @Override
    public int compareTo(BayesianMetric other) {
        if (this._spamicityValue > other._spamicityValue) {
            return 1;
        } else if (this._spamicityValue < other._spamicityValue) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return get();
    }
}
