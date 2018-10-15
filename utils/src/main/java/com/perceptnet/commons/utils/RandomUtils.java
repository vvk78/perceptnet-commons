package com.perceptnet.commons.utils;

import java.util.Random;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 30.08.2018
 */
public class RandomUtils {
    private static final Random random = new Random();

    public static long nextLong(long lowLimit, long highLimit) {
        return ((long) (random.nextDouble() * (highLimit - lowLimit))) + lowLimit;
    }

    public static int nextInt(int lowLimit, int highLimit) {
        return ((int) (random.nextDouble() * (highLimit - lowLimit))) + lowLimit;
    }

    public static double nextDouble(double lowLimit, double highLimit) {
        return (random.nextDouble() * (highLimit - lowLimit)) + lowLimit;
    }

}
