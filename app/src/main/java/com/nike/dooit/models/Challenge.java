package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Challenge implements Serializable {
    private String id;
    private String name;
    @SerializedName("activation_date")
    private DateTime activationDate;
    @SerializedName("deactivation_date")
    private DateTime deactivationDate;
    private String type;
    private List<Question> questions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(DateTime activationDate) {
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

    public DateTime getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(DateTime deactivationDate) {
        this.deactivationDate = deactivationDate;
    }
}