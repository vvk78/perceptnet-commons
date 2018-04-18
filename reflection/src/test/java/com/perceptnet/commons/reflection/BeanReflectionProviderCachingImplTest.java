package com.perceptnet.commons.reflection;

import org.testng.annotations.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.01.2018
 */
public class BeanReflectionProviderCachingImplTest {
    @Test
    public void testGetReflection() throws Exception {
        BeanReflectionProviderCachingImpl bp = new BeanReflectionProviderCachingImpl();
        BeanReflection reflection = bp.getReflection(MapGetter.class);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                               I N N E R    C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static class MapGetter<NH extends List> {
        protected final Map<Object, NH> convertableNestedVos = new LinkedHashMap<Object, NH>();

        public Collection<NH> getConveratbleNestedVoHolders() {
            return convertableNestedVos.values();
        }
    }

}