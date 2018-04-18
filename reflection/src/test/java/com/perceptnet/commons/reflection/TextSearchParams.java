package com.perceptnet.commons.reflection;


/**
 * created by vkorovkin (vkorovkin@gmail.com) on 03.09.2017
 */
public class TextSearchParams<SELF extends TextSearchParams<SELF>> implements PaginationInfo {

    private String authorName;
    private String title;
    private TextType textType;

    private Integer pageIndex;
    private Integer pageSize;

    public TextSearchParams() {
    }

    public TextSearchParams(TextType textType) {
        this.textType = textType;
    }

    public TextSearchParams(String authorName, String title) {
        this.authorName = authorName;
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public SELF setAuthorName(String authorName) {
        this.authorName = authorName;
        return (SELF) this;
    }

    public String getTitle() {
        return title;
    }

    public SELF setTitle(String title) {
        this.title = title;
        return (SELF) this;
    }

    public TextType getTextType() {
        return textType;
    }

    public SELF setTextType(TextType textType) {
        this.textType = textType;
        return (SELF) this;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
