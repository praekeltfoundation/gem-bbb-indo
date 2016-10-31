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
    private boolean correct = false;

    public long getId() {
        return id;
    }

    public String getName() { return name; }

    public String getText() {
        return text;
    }

    public boolean getCorrect() { return correct; }

    public void setName(String name) {
        this.name = name;
    }
}
