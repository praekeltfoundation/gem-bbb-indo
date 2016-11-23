package org.gem.indo.dooit.models.challenge;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by herman on 2016/11/05.
 */

public class ParticipantAnswer implements Serializable {
    private Long participant;
    private Long user;
    private Long challenge;
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

    public ParticipantAnswer(long user, long challenge, long question, long selectedOption) {
        this.user = user;
        this.challenge = challenge;
        this.question = question;
        this.selectedOption = selectedOption;
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
