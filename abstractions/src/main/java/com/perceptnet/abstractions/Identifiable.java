package com.perceptnet.abstractions;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 11.01.2018
 */
public interface Identifiable<ID> extends Identified<ID> {
    void setId(ID id);
}
