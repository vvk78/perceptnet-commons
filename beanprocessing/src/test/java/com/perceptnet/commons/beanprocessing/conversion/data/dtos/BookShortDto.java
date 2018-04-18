package com.perceptnet.commons.beanprocessing.conversion.data.dtos;

import com.perceptnet.commons.beanprocessing.conversion.data.dos.AuthorDo;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class BookShortDto extends BaseDto {
    private LookupDto author;
    private String title;

    public LookupDto getAuthor() {
        return author;
    }

    public void setAuthor(LookupDto author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
