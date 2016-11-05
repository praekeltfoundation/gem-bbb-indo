package com.nike.dooit.models;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Entry {

    private int user;
    private int challenge;
    private DateTime date_completed = new DateTime();
    private List<ParticipantAnswer> answers;

    public Entry(int user, int challenge, List<ParticipantAnswer> answers) {
        this.user = user;
        this.challenge = challenge;
        this.date_completed = new DateTime();
        this.answers = answers;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getChallenge() {
        return challenge;
    }

    public void setChallenge(int challenge) {
        this.challenge = challenge;
    }

    public DateTime getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(DateTime date_completed) {
        this.date_completed = date_completed;
    }

    public List<ParticipantAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ParticipantAnswer> answers) {
        this.answers = answers;
    }
}
