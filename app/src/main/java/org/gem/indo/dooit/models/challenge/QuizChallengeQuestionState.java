package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-11-29.
 */

public class QuizChallengeQuestionState implements Parcelable, Serializable {

    /*************
     * Variables *
     *************/

    public boolean completed = false;

    @SerializedName("option_id")
    public long optionId = -1;


    /****************
     * Constructors *
     ****************/

    public QuizChallengeQuestionState(long optionId, boolean completed) {
        this.optionId = optionId;
        this.completed = completed;
    }


    /************************
     * Parcelable interface *
     ************************/

    protected QuizChallengeQuestionState(Parcel in) {
        completed = in.readByte() != 0x00;
        optionId = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (completed ? 0x01 : 0x00));
        dest.writeLong(optionId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QuizChallengeQuestionState> CREATOR = new Parcelable.Creator<QuizChallengeQuestionState>() {
        @Override
        public QuizChallengeQuestionState createFromParcel(Parcel in) {
            return new QuizChallengeQuestionState(in);
        }

        @Override
        public QuizChallengeQuestionState[] newArray(int size) {
            return new QuizChallengeQuestionState[size];
        }
    };
}
