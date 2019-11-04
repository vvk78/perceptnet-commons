package com.perceptnet.commons.json.dto;

import com.perceptnet.abstractions.Updatable;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 01.12.2018
 */
public class BaseUpdatableDto extends BaseIdentifiedDto implements Updatable {

    public BaseUpdatableDto() {
    }

    public BaseUpdatableDto(Long id) {
        super(id);
    }
}
