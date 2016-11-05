package com.nike.dooit.models;

/**
 * Created by herman on 2016/11/05.
 */

public class Option {

    private String id;
    private String text;
    private String correct;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }
}
