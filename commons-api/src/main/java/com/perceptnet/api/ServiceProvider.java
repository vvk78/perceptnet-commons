package com.perceptnet.api;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public interface ServiceProvider {

    /**
     * Provides service of given class if its available and null otherwise.
     *
     * @param serviceClass
     * @param serviceExtraQualifiers extra parameters to resolve needed service. May be null.
     * @param <S>
     * @return
     */
    <S> S getService(Class<S> serviceClass, String serviceExtraQualifiers);
}
