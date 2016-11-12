package com.nike.dooit.models.challenge;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by herman on 2016/11/05.
 */

public class ParticipantAnswer implements Serializable {

    private long question;
    @SerializedName("selected_option")
    private long selectedOption;
    @SerializedName("date_answered")
    private DateTime dateAnswered = new DateTime();

    public ParticipantAnswer() {
        // Mandatory empty constructor
    }

    public ParticipantAnswer(long question, long selectedOption) {
        this.question = question;
        this.selectedOption = selectedOption;
        this.dateAnswered = new DateTime();
    }

    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public long getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(long selectedOption) {
        this.selectedOption = selectedOption;
    }

    public DateTime getDateAnswered() {
        return dateAnswered;
    }

    public void setDateAnswered(DateTime dateAnswered) {
        this.dateAnswered = dateAnswered;
    }
}
