package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by herman on 2016/11/05.
 */

public class ParticipantAnswer {

    private int question;
    @SerializedName("selected_option") private int selectedOption;
    @SerializedName("date_answered") private DateTime dateAnswered = new DateTime();

    public ParticipantAnswer(int question, int selectedOption) {
        this.question = question;
        this.selectedOption = selectedOption;
        this.dateAnswered = new DateTime();
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public DateTime getDateAnswered() {
        return dateAnswered;
    }

    public void setDateAnswered(DateTime dateAnswered) {
        this.dateAnswered = dateAnswered;
    }
}
