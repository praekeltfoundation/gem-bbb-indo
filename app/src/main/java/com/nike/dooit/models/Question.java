package com.nike.dooit.models;

import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Question
{
    private String id;
    private String text;
    private List<Option> options;

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

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}