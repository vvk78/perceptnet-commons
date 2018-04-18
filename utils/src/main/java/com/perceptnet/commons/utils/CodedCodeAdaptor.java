package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.Adaptor;
import com.perceptnet.abstractions.Coded;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.02.2018
 */
public class CodedCodeAdaptor<C extends Comparable<C>> implements Adaptor<Coded<C>, C> {
    public static final CodedCodeAdaptor I = new CodedCodeAdaptor();

    @Override
    public C adapt(Coded<C> c) {
        return c == null ? null : c.getCode();
    }
}
