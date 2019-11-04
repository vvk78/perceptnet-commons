package com.perceptnet.commons.json.dto;

import com.perceptnet.abstractions.LongIdentifiable;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 15.11.2018
 */
public class BaseIdentifiedDto implements LongIdentifiable {
    private Long id;

    public BaseIdentifiedDto() {
    }

    public BaseIdentifiedDto(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + ", " + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseIdentifiedDto that = (BaseIdentifiedDto) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
