package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.Adaptor;
import com.perceptnet.abstractions.Named;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.02.2018
 */
public class NamedNameAdaptor implements Adaptor<Named, String> {
    public static final NamedNameAdaptor I = new NamedNameAdaptor();

    @Override
    public String adapt(Named named) {
        return named == null ? null : named.getName();
    }
}
