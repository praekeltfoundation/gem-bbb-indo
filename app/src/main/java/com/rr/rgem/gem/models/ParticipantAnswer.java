package com.rr.rgem.gem.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Rudolph Jacobs on 2016-10-28.
 */

public class ParticipantAnswer {
    long question;

    @SerializedName("selected_option")
    long selectedOption;

    @SerializedName("date_answered")
    Date dateAnswered = new Date();

    public ParticipantAnswer(Question question, Answer selectedOption)
    {
        this.question = question.getId();
        this.selectedOption = selectedOption.getId();
        this.dateAnswered = new Date();
    }

    public ParticipantAnswer(long question, long selectedOption)
    {
        this.question = question;
        this.selectedOption = selectedOption;
        this.dateAnswered = new Date();
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

    public Date getDateAnswered() {
        return dateAnswered;
    }

    public void setDateAnswered(Date dateAnswered) {
        this.dateAnswered = dateAnswered;
    }
}
