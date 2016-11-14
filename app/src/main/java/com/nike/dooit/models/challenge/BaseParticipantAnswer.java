package com.nike.dooit.models.challenge;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-14.
 */

public abstract class BaseParticipantAnswer {
    protected Long participant;
    protected Long user;
    protected Long challenge;
    protected long question;
    @SerializedName("date_answered")
    protected DateTime dateAnswered = new DateTime();

    public BaseParticipantAnswer() {
        // Mandatory empty constructor
    }

    public BaseParticipantAnswer(long user, long challenge) {
        this.user = user;
        this.challenge = challenge;
        this.dateAnswered = new DateTime();
    }

    public long getParticipant() {
        return participant;
    }

    public void setParticipant(long participant) {
        this.participant = participant;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getChallenge() {
        return challenge;
    }

    public void setChallenge(long challenge) {
        this.challenge = challenge;
    }

    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public DateTime getDateAnswered() {
        return dateAnswered;
    }

    public void setDateAnswered(DateTime dateAnswered) {
        this.dateAnswered = dateAnswered;
    }
}
