package com.perceptnet.api;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public interface ServiceProvider {

    /**
     * Provides service of given class
     * @param serviceClass
     * @param servicePath extra parameters to resolve needed service
     * @param <S>
     * @return
     */
    <S> S getService(Class<S> serviceClass, String servicePath);
}
