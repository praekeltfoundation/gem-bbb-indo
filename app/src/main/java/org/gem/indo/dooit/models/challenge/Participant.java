package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-11-24.
 */

public class Participant implements Parcelable, Serializable {
    /*************
     * Variables *
     *************/
    private Long challenge = null;
    private Long id = null;
    private Long user = null;


    /****************
     * Constructors *
     ****************/

    public Participant() {
        // Mandatory empty constructor
    }

    public Participant(long user, long challenge) {
        this.user = user;
        this.challenge = challenge;
    }


    /***********
     * Getters *
     ***********/

    public long getChallenge() {
        return challenge != null ? challenge : -1;
    }

    public long getId() {
        return id != null ? id : -1;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(long user) {
        this.user = user;
    }


    /************************
     * Parcelable interface *
     ************************/

    Participant(Parcel in) {
        if (in.readByte() != 0x0) {
            this.challenge = in.readLong();
        }
        if (in.readByte() != 0x0) {
            this.id = in.readLong();
        }
        if (in.readByte() != 0x0) {
            this.user = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (challenge != null) {
            dest.writeByte((byte) 0x01);
            dest.writeLong(challenge);
        } else {
            dest.writeByte((byte) 0x00);
        }
        if (id != null) {
            dest.writeByte((byte) 0x01);
            dest.writeLong(id);
        } else {
            dest.writeByte((byte) 0x00);
        }
        if (user != null) {
            dest.writeByte((byte) 0x01);
            dest.writeLong(user);
        } else {
            dest.writeByte((byte) 0x00);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Participant> CREATOR = new Parcelable.Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };
}
