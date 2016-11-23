package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public class QuizChallengeOption implements Parcelable, Serializable {
    private long id;
    private String text;
    private boolean correct;

    QuizChallengeOption() {
        // Mandatory empty constructor
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

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    protected QuizChallengeOption(Parcel in) {
        id = in.readLong();
        text = in.readString();
        correct = in.readByte() != 0x0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(text);
        dest.writeByte((byte) (correct ? 0x1 : 0x0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QuizChallengeOption> CREATOR = new Parcelable.Creator<QuizChallengeOption>() {
        @Override
        public QuizChallengeOption createFromParcel(Parcel in) {
            return new QuizChallengeOption(in);
        }

        @Override
        public QuizChallengeOption[] newArray(int size) {
            return new QuizChallengeOption[size];
        }
    };
}
