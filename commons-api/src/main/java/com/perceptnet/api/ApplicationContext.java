package com.perceptnet.api;

import java.util.concurrent.atomic.AtomicReference;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public class ApplicationContext {
    public static AtomicReference<? extends ServiceProvider> serviceProviderHolder = new AtomicReference<>(new SimplisticServiceProvider());

    public static <S> S getService(Class<S> serviceClass, String servicePath) {
        ServiceProvider sp = serviceProviderHolder.get();
        if (sp == null) {
            return null;
        }
        return sp.getService(serviceClass, servicePath);
    }


}
