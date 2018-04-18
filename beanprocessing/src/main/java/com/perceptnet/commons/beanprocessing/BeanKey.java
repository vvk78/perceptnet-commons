package com.perceptnet.commons.beanprocessing;

import com.perceptnet.abstractions.Identified;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 23.08.2017
 */
public class BeanKey {
    private final Object id;
    private final Class beanClass;

    public BeanKey(Identified dto) {
        this(dto.getId(), dto.getClass());
    }

    public BeanKey(Object id, Class beanClass) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        if (beanClass == null) {
            throw new NullPointerException("beanClass is null");
        }
        this.id = id;
        this.beanClass = beanClass;
    }

    @Override
    public boolean equals(Object o) {
        //auto generated method
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanKey dtoKey = (BeanKey) o;

        if (!beanClass.equals(dtoKey.beanClass)) return false;
        if (!id.equals(dtoKey.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        //auto generated method
        int result = id.hashCode();
        result = 31 * result + beanClass.hashCode();
        return result;
    }
}
