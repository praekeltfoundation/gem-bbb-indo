package com.rr.rgem.gem.models;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class TipArticle {

    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("TipArticle{%s}", title);
    }
}
