package com.perceptnet.api;

import java.util.Collection;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public interface ItemsSaveService {
    void saveItem(String fileName, Object item);

    /**
     * Saves collection of items in specified file. File is to be (re-)created if does not exists.
     */
    void saveItems(String fileName, Collection items);
}
