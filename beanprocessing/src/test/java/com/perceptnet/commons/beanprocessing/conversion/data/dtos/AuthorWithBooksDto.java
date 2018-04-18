package com.perceptnet.commons.beanprocessing.conversion.data.dtos;

import com.perceptnet.abstractions.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class AuthorWithBooksDto extends BaseDto implements Named {
    private String name;

    private List<BookShortDto> books = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookShortDto> getBooks() {
        return books;
    }
}
