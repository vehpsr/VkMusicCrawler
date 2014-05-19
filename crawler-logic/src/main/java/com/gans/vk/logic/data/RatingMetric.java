package com.gans.vk.logic.data;

import java.text.DecimalFormat;

public class RatingMetric implements Metric, Comparable<RatingMetric> {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#.00");
    private float _rating;

    public RatingMetric(float rating) {
        _rating = rating;
    }

    @Override
    public String get() {
        return FORMATTER.format(_rating * 100);
    }

    @Override
    public int compareTo(RatingMetric other) {
        if (this._rating > other._rating) {
            return 1;
        } else if (this._rating < other._rating) {
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
