package com.daniel366cobra.impenitent_illagers.util;

public class MathHelper {

    /**
     * Maps a float value from [inputStart - inputEnd] range to [outputStart - outputEnd] range.
     * @param inputValue value to map
     * @param inputStart lower bound of input range
     * @param inputEnd upper bound of input range
     * @param outputStart lower bound of output range
     * @param outputEnd upper bound of output range
     * @return value mapped to output range bounds
     */
    public static float map(float inputValue, float inputStart, float inputEnd, float outputStart, float outputEnd) {
        if (inputValue < inputStart || inputValue > inputEnd) throw new IllegalArgumentException("Input value must be within input range bounds.");
        if (inputEnd == inputStart || outputEnd == outputStart) throw new IllegalArgumentException("Range size must not be zero.");

        return (inputValue - inputStart) * (outputEnd - outputStart) / (inputEnd - inputStart) + outputStart;
    }
}
