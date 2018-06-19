package com.perceptnet.api;

import java.util.concurrent.atomic.AtomicReference;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public class Services {
    public static AtomicReference<? extends ServiceProvider> serviceProviderHolder = new AtomicReference<>(new SimplisticServiceProvider());

    /**
     * Returns appropriate service implementation if it is available.
     *
     * @param serviceClass class of required service
     * @param serviceExtraQualifiers additional qualifiers to get most appropriate service impl. May be null.
     */
    public static <S> S get(Class<S> serviceClass, String serviceExtraQualifiers) {
        ServiceProvider sp = serviceProviderHolder.get();
        if (sp == null) {
            return null;
        }
        return sp.getService(serviceClass, serviceExtraQualifiers);
    }


}
