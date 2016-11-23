package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import org.gem.indo.dooit.models.enums.ChallengeType;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public class FreeformChallenge extends BaseChallenge {
    @SerializedName("freetext_question") FreeformChallengeQuestion question;

    FreeformChallenge() {
        // Mandatory empty constructor
        super();
        this.type = ChallengeType.FREEFORM;
    }

    public FreeformChallengeQuestion getQuestion() {
        return question;
    }

    public void setQuestion(FreeformChallengeQuestion question) {
        this.question = question;
    }

    protected FreeformChallenge(Parcel in) {
        super(in);
        if (in.readByte() == 0x1) {
            question = in.readParcelable(FreeformChallengeQuestion.class.getClassLoader());
        } else {
            question = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        if (question == null) {
            dest.writeByte((byte) 0x0);
        } else {
            dest.writeByte((byte) 0x1);
            dest.writeParcelable(question, flags);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FreeformChallenge> CREATOR = new Parcelable.Creator<FreeformChallenge>() {
        @Override
        public FreeformChallenge createFromParcel(Parcel in) {
            return new FreeformChallenge(in);
        }

        @Override
        public FreeformChallenge[] newArray(int size) {
            return new FreeformChallenge[size];
        }
    };
}
