package com.perceptnet.commons.json.dto;

import com.perceptnet.abstractions.Versionable;

/**
 * This base class is to be used as an ancestor for data that can be updated in db <b>but not concurrently</b>. I.e. there is a check
 * that no other user changed this data item since it was loaded. i.e. Optimistic Locking
 *
 * created by vkorovkin (vkorovkin@gmail.com) on 01.12.2018
 */
public class BaseVersionedDto extends BaseUpdatableDto implements Versionable {
    private int version;

    public BaseVersionedDto() {
    }

    public BaseVersionedDto(Long id) {
        super(id);
    }

    public BaseVersionedDto(Long id, Integer version) {
        super(id);
        this.version = version;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }
}
