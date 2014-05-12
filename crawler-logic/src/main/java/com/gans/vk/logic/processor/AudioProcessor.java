package com.gans.vk.logic.processor;

import java.text.MessageFormat;

import com.gans.vk.data.AudioLibrary;

public interface AudioProcessor {

    String getProcessorDescription();

    DiversityStatistics getDiversityStatistics(AudioLibrary lib);

    public static class DiversityStatistics {
        public static final float PARTIAL_DIVERSITY_PERCENTAGE = 0.05f;
        public static final String LEGEND = MessageFormat.format("ad - absolute diversity\npd - partial diversity (contribution to audio collection by {0}% of top library atrists)", percentageOf(PARTIAL_DIVERSITY_PERCENTAGE));

        private int _absoluteDiversity;
        private int _partialDiversity;

        public int getAbsoluteDiversityPercentage() {
            return _absoluteDiversity;
        }

        public void setAbsoluteDiversity(float absoluteDiversity) {
            _absoluteDiversity = percentageOf(absoluteDiversity);
        }

        public float getPartialDiversityPercentage() {
            return _partialDiversity;
        }

        public void setPartialDiversity(float partialDiversity) {
            _partialDiversity = percentageOf(partialDiversity);
        }

        private static int percentageOf(float f) {
            return Math.round(f * 100);
        }

        public String format() {
            return MessageFormat.format("ad {0}%, pd {1}%", _absoluteDiversity, _partialDiversity);
        }

        @Override
        public String toString() {
            return format();
        }
    }
}
