package com.perceptnet.commons.beanprocessing.conversion;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 18.01.2019
 */
public interface JpaConversionHelper<ID>  {

    Object getOne(ID id, Class destClass);

    ID save(Object item);

    void delete(Object item);

}
