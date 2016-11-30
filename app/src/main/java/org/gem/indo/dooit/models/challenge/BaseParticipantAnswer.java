package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-14.
 */

public abstract class BaseParticipantAnswer implements Parcelable {

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

    public DateTime getDateAnswered() {
        return dateAnswered;
    }

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

    /************************
     * Parcelable interface *
     ************************/

    protected BaseParticipantAnswer(Parcel in) {
        dateAnswered = (DateTime) in.readValue(DateTime.class.getClassLoader());
        challenge = in.readByte() == 0x00 ? null : in.readLong();
        question = in.readByte() == 0x00 ? null : in.readLong();
        participant = in.readByte() == 0x00 ? null : in.readLong();
        user = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dateAnswered);
        if (challenge == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(challenge);
        }
        if (question == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(question);
        }
        if (participant == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(participant);
        }
        if (user == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(user);
        }
    }
}
