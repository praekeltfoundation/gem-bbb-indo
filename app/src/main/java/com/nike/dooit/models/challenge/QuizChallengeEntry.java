package com.nike.dooit.models.challenge;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class QuizChallengeEntry implements Serializable {

    private long user;
    private long challenge;
    @SerializedName("date_completed") private DateTime dateCompleted = new DateTime();
    private List<ParticipantAnswer> answers;

    public QuizChallengeEntry() {
        // Mandatory empty constructor
    }

    public QuizChallengeEntry(long user, long challenge, List<ParticipantAnswer> answers) {
        this.user = user;
        this.challenge = challenge;
        this.dateCompleted = new DateTime();
        this.answers = answers;
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

    public DateTime getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(DateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public List<ParticipantAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ParticipantAnswer> answers) {
        this.answers = answers;
    }
}
