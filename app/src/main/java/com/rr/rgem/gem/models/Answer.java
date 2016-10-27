package com.rr.rgem.gem.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 9/14/2016.
 */
public class Answer {
    private long id;
    private String name;
    private String text;

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getText() {
        return text;
    }

    public void setName(String name) {
        this.name = name;
    }
}
