package com.perceptnet.commons.timeseries;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 24.08.2018
 */
class PositionInfo {
    /**
     * "Less or equal" ts. Or null if there is nothing
     */
    long precedingTs;
    long precedingTsValue;
    long precedingTsPosition;

    PositionInfo(long precedingTs, long precedingTsValue, long precedingTsPosition) {
        this.precedingTs = precedingTs;
        this.precedingTsValue = precedingTsValue;
        this.precedingTsPosition = precedingTsPosition;
    }


}
