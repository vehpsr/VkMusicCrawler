package com.gans.vk.logic.processor.impl;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

import com.gans.vk.data.ArtistData;
import com.gans.vk.data.AudioLibrary;
import com.gans.vk.logic.data.BayesianMetric;
import com.gans.vk.logic.data.Metric;
import com.gans.vk.logic.service.BayesianService;

public class BayesianAudioProcessor extends GenericAudioProcessor {

    public BayesianAudioProcessor(BayesianService monochromeList) {
        super(monochromeList);
    }

    @Override
    public String getDescription() {
        return "Processor that use Bayesian probability to filter audio library";
    }

    @Override
    public Entry<String, Metric> evaluate(AudioLibrary lib) {
        List<Double> spam = new ArrayList<Double>();
        for (ArtistData data : lib.getEntries()) {
            double spamicity = _bayesianService.spamicity(data);
            spam.add(spamicity);
        }
        Collections.sort(spam, new Comparator<Double>() {
            /*
             * sort by how different spamicity is from neutral (0.5)
             * i.e. 50/50 is as boring as it gets
             */
            @Override
            public int compare(Double d1, Double d2) {
                final double PRECISION = 0.00001;
                double d1SpamInfluence = Math.abs(BayesianService.SPAMICITY_NEUTRAL - d1);
                double d2SpamInfluence = Math.abs(BayesianService.SPAMICITY_NEUTRAL - d2);
                if (Math.abs(d1SpamInfluence - d2SpamInfluence) < PRECISION) {
                    return 0;
                } else if (d1SpamInfluence > d2SpamInfluence) {
                    return -1;
                } else if (d1SpamInfluence < d2SpamInfluence) {
                    return 1;
                } else {
                    throw new AssertionError(MessageFormat.format("Fail to compare {0} and {1}", d1, d2));
                }
            }
        });

        BayesianMetric metric;
        final int PRECISION_LIMIT = 100; // use only top {n} of most influential spamicity coefficients
        if (spam.size() < PRECISION_LIMIT) {
            metric = calculateAudioLibrarySpamicity(spam);
        } else {
            metric = calculateAudioLibrarySpamicity(spam.subList(0, PRECISION_LIMIT));
        }
        return new AbstractMap.SimpleEntry<String, Metric>(lib.getId(), metric);
    }

    private BayesianMetric calculateAudioLibrarySpamicity(List<Double> spam) {
        double positiveProduct = 1.0;
        double negativeProduct = 1.0;
        for (Double val : spam) {
            positiveProduct *= val;
            negativeProduct *= (1.0 - val);
        }
        return new BayesianMetric(positiveProduct / (positiveProduct + negativeProduct));
    }
}
