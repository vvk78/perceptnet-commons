package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.IndexedAccess;

/**
 * created by vkorovkin on 07.05.2018
 */
public class StringIndexedAccess implements IndexedAccess<Character> {
    private final String str;

    public StringIndexedAccess(String str) {
        this.str = str;
    }

    @Override
    public int size() {
        return str.length();
    }

    @Override
    public Character get(int i) {
        return str.charAt(i);
    }
}
