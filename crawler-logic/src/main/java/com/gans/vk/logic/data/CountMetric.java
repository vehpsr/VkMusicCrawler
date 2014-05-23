package com.gans.vk.logic.data;

public class CountMetric implements Metric {

    private int _count;

    public CountMetric(int count) {
        _count = count;
    }

    @Override
    public String get() {
        return String.valueOf(_count);
    }

    @Override
    public String toString() {
        return get();
    }
}
