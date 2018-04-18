package com.perceptnet.commons.beanprocessing.conversion.data.dos;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class BookDo extends BaseDo {
    private AuthorDo author;
    private String title;
    private int userRating;

    public AuthorDo getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDo author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }
}
