package com.nike.dooit.models;

import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Challenge {
    private String id;
    private String activation_date;
    private String name;
    private List<Question> questions;
    private String type;
    private String deactivation_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivation_date() {
        return activation_date;
    }

    public void setActivation_date(String activation_date) {
        this.activation_date = activation_date;
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

    public String getDeactivation_date() {
        return deactivation_date;
    }

    public void setDeactivation_date(String deactivation_date) {
        this.deactivation_date = deactivation_date;
    }
}