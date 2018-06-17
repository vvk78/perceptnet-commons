package com.perceptnet.api;

import java.util.Collection;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public interface ItemsLoadService {
    Object loadItem(String fileOrResourceName);

    /**
     * Loads collection of items from file or resource. Resource name is indicated by 'classpath:' prefix.
     * @param fileOrResourceName Name of file or resource. Resource name is indicated by 'classpath:' prefix.
     * @return
     */
    Collection loadItems(String fileOrResourceName);
}
