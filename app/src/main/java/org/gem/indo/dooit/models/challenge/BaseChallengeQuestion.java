package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public abstract class BaseChallengeQuestion implements Parcelable, Serializable {
    private long id;
    private String text;

    BaseChallengeQuestion() {
        // Mandatory empty constructor
    }

    protected BaseChallengeQuestion(Parcel in) {
        id = in.readLong();
        text = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(text);
    }
}
