package com.perceptnet.api;

import java.util.concurrent.ConcurrentHashMap;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
class SimplisticServiceProvider implements ServiceProvider {
    private final ConcurrentHashMap<String, Object> knownImplementationsMap = new ConcurrentHashMap<String, Object>();

    @Override
    public <S> S getService(Class<S> serviceClass, String servicePath) {
        return (S) knownImplementationsMap.get(serviceKey(serviceClass, servicePath));
    }

    public void registerImpl(Class serviceClass, String servicePath, Object serviceImpl) {
        knownImplementationsMap.put(serviceKey(serviceClass, servicePath), serviceImpl);
    }

    private String serviceKey(Class serviceClass, String servicePath) {
        return serviceClass + " " + servicePath;
    }

}
