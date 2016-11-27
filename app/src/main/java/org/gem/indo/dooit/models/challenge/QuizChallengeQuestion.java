package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class QuizChallengeQuestion extends BaseChallengeQuestion
{
    @SuppressWarnings("unused")
    public static final Creator<QuizChallengeQuestion> CREATOR = new Creator<QuizChallengeQuestion>() {
        @Override
        public QuizChallengeQuestion createFromParcel(Parcel in) {
            return new QuizChallengeQuestion(in);
        }

        @Override
        public QuizChallengeQuestion[] newArray(int size) {
            return new QuizChallengeQuestion[size];
        }
    };
    /*** Variables ***/

    private List<QuizChallengeOption> options;
    private String hint;

    /*** Parcelable methods ***/

    protected QuizChallengeQuestion(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            options = new ArrayList<QuizChallengeOption>();
            in.readList(options, QuizChallengeOption.class.getClassLoader());
        } else {
            options = null;
        }
        if (in.readByte() == 0x01) {
            hint = in.readString();
        } else {
            hint = null;
        }
    }

    /*** Getters/Setters ***/

    public List<QuizChallengeOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuizChallengeOption> options) {
        this.options = options;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        if (options == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(options);
        }
        if (hint == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(hint);
        }
    }
}