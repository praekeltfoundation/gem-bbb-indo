package org.gem.indo.dooit.models.challenge;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-14.
 */

public abstract class BaseParticipantAnswer {

    /*************
     * Variables *
     *************/

    @SerializedName("date_answered")
    protected DateTime dateAnswered = new DateTime();
    protected Long challenge;
    protected Long question;
    protected Long participant;
    protected Long user;


    /****************
     * Constructors *
     ****************/

    public BaseParticipantAnswer() {
        // Mandatory empty constructor
    }

    public BaseParticipantAnswer(long user, long challenge) {
        this.user = user;
        this.challenge = challenge;
        this.dateAnswered = new DateTime();
    }


    /***********
     * Getters *
     ***********/

    public long getChallenge() {
        return challenge != null ? challenge : -1;
    }

    public long getParticipant() {
        return participant != null ? participant : -1;
    }

    public long getQuestion() {
        return question != null ? question : -1;
    }

    public long getUser() {
        return user != null ? user : -1;
    }

    public DateTime getDateAnswered() {
        return dateAnswered;
    }


    /***********
     * Setters *
     ***********/

    public void setChallenge(long challenge) {
        this.challenge = challenge;
    }

    public void setDateAnswered(DateTime dateAnswered) {
        this.dateAnswered = dateAnswered;
    }

    public void setParticipant(long participant) {
        this.participant = participant;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
