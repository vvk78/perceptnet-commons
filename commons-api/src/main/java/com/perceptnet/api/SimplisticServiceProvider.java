package com.perceptnet.api;

import com.perceptnet.commons.utils.ClassUtils;

import java.util.concurrent.ConcurrentHashMap;
import static com.perceptnet.api.SimplisticServiceProvider.ServicePathPair.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
class SimplisticServiceProvider implements ServiceProvider {
    private final ConcurrentHashMap<String, Object> knownImplementationsMap = new ConcurrentHashMap<String, Object>();

    SimplisticServiceProvider() {
    }

    @Override
    public <S> S getService(Class<S> serviceClass, String servicePath) {
        return (S) knownImplementationsMap.get(serviceKey(serviceClass, servicePath));
    }

    public void registerImpl(Class serviceClass, String servicePath, Object serviceImpl) {
        knownImplementationsMap.put(serviceKey(serviceClass, servicePath), serviceImpl);
    }

    private String serviceKey(Class serviceClass, String servicePath) {
        return serviceClass.getName() + " " + servicePath;
    }

    private void registerKnownDefaultMappingsWhenAvailable() {
        proble("com.perceptnet.commons.json.JsonService", paths(null, "json"), spp(ItemsLoadService.class), spp(ItemsSaveService.class));
    }

    private void proble(String className, String[] commonPaths, ServicePathPair ... servicesAndPaths) {
        Object impl = ClassUtils.createSafely(className);
        if (impl == null) {
            return;
        }
        for (ServicePathPair spp : servicesAndPaths) {
            if (spp.service.isAssignableFrom(impl.getClass())) {
                if (commonPaths != null) {
                    for (String commonPath : commonPaths) {
                        registerImpl(spp.service, commonPath, impl);
                    }
                }
                if (spp.paths != null) {
                    for (String path : spp.paths) {
                        registerImpl(spp.service, path, impl);
                    }
                }
            }
        }
    }

    private String[] paths(String ... paths) {
        return paths;
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                               I N N E R    C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    static final class ServicePathPair {
        Class<?> service;
        String[] paths;

        private ServicePathPair(Class service, String ... paths) {
            this.service = service;
            this.paths = paths;
        }

        static ServicePathPair spp(Class service, String ... paths) {
            return new ServicePathPair(service, paths);
        }

        static ServicePathPair spp(Class service) {
            return new ServicePathPair(service, null);
        }
    }

}
