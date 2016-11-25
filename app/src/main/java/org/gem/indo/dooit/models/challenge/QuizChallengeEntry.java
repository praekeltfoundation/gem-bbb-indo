package org.gem.indo.dooit.models.challenge;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class QuizChallengeEntry implements Serializable {

    /*************
     * Variables *
     *************/

    @SerializedName("date_completed")
    private DateTime dateCompleted = new DateTime();
    private List<ParticipantAnswer> answers;
    private Long challenge = null;
    private Long participant = null;
    private Long user = null;


    /****************
     * Constructors *
     ****************/

    public QuizChallengeEntry() {
        // Mandatory empty constructor
    }

    public QuizChallengeEntry(long participant, List<ParticipantAnswer> answers) {
        this.participant = participant;
        this.dateCompleted = new DateTime();
        this.answers = answers;
    }

    public QuizChallengeEntry(long user, long challenge, List<ParticipantAnswer> answers) {
        this.user = user;
        this.challenge = challenge;
        this.dateCompleted = new DateTime();
        this.answers = answers;
    }


    /***********
     * Getters *
     ***********/

    public DateTime getDateCompleted() {
        return dateCompleted;
    }

    public List<ParticipantAnswer> getAnswers() {
        return answers;
    }

    public long getChallenge() {
        return challenge != null ? challenge : -1;
    }

    public long getParticipant() {
        return participant != null ? participant : -1;
    }

    public long getUser() {
        return user != null ? user : -1;
    }

    /***********
     * Setters *
     ***********/

    public void setAnswers(List<ParticipantAnswer> answers) {
        this.answers = answers;
    }

    public void setChallenge(long challenge) {
        this.challenge = challenge;
    }

    public void setDateCompleted(DateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public void setParticipant(long participant) {
        this.participant = participant;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
