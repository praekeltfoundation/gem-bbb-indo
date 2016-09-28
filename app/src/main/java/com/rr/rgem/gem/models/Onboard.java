package com.rr.rgem.gem.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjj98 on 9/20/2016.
 */
public class Onboard {

    String name;
    String completed;
    List<Question> questions = new ArrayList<Question>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }
}
