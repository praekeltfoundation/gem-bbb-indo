package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Challenge {
    private String id;
    private String name;
    @SerializedName("activation_date") private String activationDate;
    @SerializedName("deactivation_date") private String deactivationDate;
    private String type;
    private List<Question> questions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(String deactivationDate) {
        this.deactivationDate = deactivationDate;
    }
}