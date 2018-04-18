package com.perceptnet.commons.beanprocessing.conversion.data.dtos;

import com.perceptnet.abstractions.Identified;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class BaseDto implements Identified<Long> {
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
