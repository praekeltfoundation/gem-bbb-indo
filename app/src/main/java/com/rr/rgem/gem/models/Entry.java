package com.rr.rgem.gem.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-01.
 */

public class Entry {
    private Long participant = null;

    private Long challenge = null;

    private Long user = null;

    @SerializedName("date_completed")
    private DateTime dateCompleted = new DateTime();

    private List<ParticipantAnswer> answers = new ArrayList<>();

    public Entry(long participant)
    {
        this.participant = participant;
    }

    public Entry(long user, long challenge)
    {
        this.user = user;
        this.challenge = challenge;
    }

    public long getParticipant() { return this.participant; }

    public long getChallenge() { return this.challenge; }

    public long getUser() { return this.user; }

    public DateTime getDateCompleted() { return this.dateCompleted; }

    public List<ParticipantAnswer> getAnswers() { return this.answers; }

    public boolean userSpecified() { return this.user != null; }

    public boolean participantSpecified() { return this.participant != null; }
}
