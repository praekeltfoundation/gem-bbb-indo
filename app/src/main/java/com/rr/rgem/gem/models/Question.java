package com.rr.rgem.gem.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chris on 9/14/2016.
 */
public class Question {
    private long id;
    private String name;
    private long order;
    private String text;
    private boolean completed = false;

    private List<Answer> answers = new ArrayList<Answer>();

    public Question() {
    }

    public Question(String text) {
        this.text = text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer>  answers) {
        this.answers = answers;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
