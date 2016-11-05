package com.rr.rgem.gem.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chris on 9/14/2016.
 */
public class Challenge {

    private long id;

    private String name;

    @SerializedName("activation_date")
    private Date activationDate;

    @SerializedName("deactivation_date")
    private Date deactivationDate;

    private String type;

    private boolean completed = false;

    private List<Question> questions = new ArrayList<Question>();

    public long getId() { return this.id; }

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

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "name='" + name + '\'' +
                '}';
    }
}
