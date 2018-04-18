package com.perceptnet.commons.beanprocessing.conversion.data.dos;

import com.perceptnet.abstractions.Identified;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class BaseDo implements Identified<Long> {
    private Long id;

    public BaseDo() {
        super();
    }

    public BaseDo(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
