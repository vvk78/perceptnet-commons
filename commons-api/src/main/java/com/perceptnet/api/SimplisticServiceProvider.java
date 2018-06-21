package com.perceptnet.api;

import com.perceptnet.commons.utils.ClassUtils;

import java.util.concurrent.ConcurrentHashMap;
import static com.perceptnet.api.SimplisticServiceProvider.ServiceQualsPair.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
class SimplisticServiceProvider implements ServiceProvider {
    private final ConcurrentHashMap<String, Object> knownImplementationsMap = new ConcurrentHashMap<String, Object>();

    SimplisticServiceProvider() {
        registerKnownDefaultMappingsWhenAvailable();
    }

    @Override
    public <S> S getService(Class<S> serviceClass, String serviceExtraQualifiers) {
        return (S) knownImplementationsMap.get(serviceKey(serviceClass, serviceExtraQualifiers));
    }

    public void registerImpl(Class serviceClass, String serviceQualifiers, Object serviceImpl) {
        knownImplementationsMap.put(serviceKey(serviceClass, serviceQualifiers), serviceImpl);
    }

    private String serviceKey(Class serviceClass, String serviceQualifiers) {
        return serviceClass.getName() + " " + serviceQualifiers;
    }

    private void registerKnownDefaultMappingsWhenAvailable() {
        probe("com.perceptnet.commons.json.JsonService", quals(null, "json"), sqp(ItemsLoadService.class), sqp(ItemsSaveService.class));
    }

    private void probe(String className, String[] commonQuals, ServiceQualsPair... servicesAndPaths) {
        Object impl = ClassUtils.createSafely(className);
        if (impl == null) {
            return;
        }
        for (ServiceQualsPair spp : servicesAndPaths) {
            if (spp.service.isAssignableFrom(impl.getClass())) {
                //register service with common qualifiers
                if (commonQuals != null) {
                    for (String q : commonQuals) {
                        registerImpl(spp.service, q, impl);
                    }
                }
                //register service with specific qualifiers
                if (spp.quals != null) {
                    for (String q : spp.quals) {
                        registerImpl(spp.service, q, impl);
                    }
                }
            }
        }
    }

    private String[] quals(String ... paths) {
        return paths;
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                               I N N E R    C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    static final class ServiceQualsPair {
        Class<?> service;
        String[] quals;

        private ServiceQualsPair(Class service, String ... quals) {
            this.service = service;
            this.quals = quals;
        }

        static ServiceQualsPair sqp(Class service, String ... paths) {
            return new ServiceQualsPair(service, paths);
        }

        static ServiceQualsPair sqp(Class service) {
            return new ServiceQualsPair(service, null);
        }
    }

}
