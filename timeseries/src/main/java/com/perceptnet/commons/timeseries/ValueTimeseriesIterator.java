package com.perceptnet.commons.timeseries;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 15.08.2018
 */
public interface ValueTimeseriesIterator {
    int fetch(long[] times, double[] values);

}
