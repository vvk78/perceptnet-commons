package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.reflection.BeanReflectionProviderCachingImpl;
import com.perceptnet.commons.reflection.ReflectionProvider;

/**
 * created by vkorovkin on 24.05.2018
 */
public interface ReflectionCache {
    ReflectionProvider DTO_REF_PROVIDER = new BeanReflectionProviderCachingImpl();
    ReflectionProvider DO_REF_PROVIDER = new BeanReflectionProviderCachingImpl();
}
