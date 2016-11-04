package com.rr.rgem.gem.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-04.
 */

public class FreeformAnswer {
    private Long id = null;

    private Long user = null;

    private Long challenge = null;

    private Long participant = null;

    private Long question = null;

    @SerializedName("date_answered")
    private DateTime dateAnswered = null;

    @SerializedName("date_saved")
    private DateTime dateSaved = null;

    private String text = null;

    public FreeformAnswer() {}

    public FreeformAnswer(long user, long challenge, DateTime dateAnswered, String text) {
        this.user = user;
        this.challenge = challenge;
        this.dateAnswered = dateAnswered;
        this.text = text;
    }

    // Getters
    public long getChallenge() { return challenge; }
    public DateTime getDateAnswered() { return dateAnswered; }
    public DateTime getDateSaved() { return dateSaved; }
    public long getId() { return id; }
    public long getParticipant() { return participant; }
    public long getQuestion() { return question; }
    public String getText() { return text; }
    public long getUser() { return user; }

    // Setters
    public void setDateAnswered(DateTime dateAnswered) { this.dateAnswered = dateAnswered; }
    public void setText(String text) { this.text = text; }
}
