package com.perceptnet.commons.beanprocessing.conversion.data.dtos;

import com.perceptnet.abstractions.Named;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class LookupDto extends BaseDto implements Named {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
