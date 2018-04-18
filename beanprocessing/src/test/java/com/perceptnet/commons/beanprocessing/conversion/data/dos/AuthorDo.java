package com.perceptnet.commons.beanprocessing.conversion.data.dos;

import com.perceptnet.abstractions.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class AuthorDo extends BaseDo implements Named {
    private String name;
    private List<BookDo> books = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookDo> getBooks() {
        return books;
    }
}
