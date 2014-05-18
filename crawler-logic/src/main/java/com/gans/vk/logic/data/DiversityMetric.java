package com.gans.vk.logic.data;

public class DiversityMetric implements Metric, Comparable<DiversityMetric> {

    private final float _percentageValue;

    public DiversityMetric(float value) {
        _percentageValue = value;
    }

    @Override
    public String get() {
        return String.valueOf(Math.round(_percentageValue * 100));
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public int compareTo(DiversityMetric other) {
        if (this._percentageValue > other._percentageValue) {
            return -1;
        } else if (this._percentageValue < other._percentageValue) {
            return 1;
        } else {
            return 0;
        }
    }
}
