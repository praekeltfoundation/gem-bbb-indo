package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.enums.ChallengeType;

/**
 * Created by Rudolph Jacobs on 2016-11-29.
 */

public class PictureChallenge extends BaseChallenge {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PictureChallenge> CREATOR = new Parcelable.Creator<PictureChallenge>() {
        @Override
        public PictureChallenge createFromParcel(Parcel in) {
            return new PictureChallenge(in);
        }

        @Override
        public PictureChallenge[] newArray(int size) {
            return new PictureChallenge[size];
        }
    };
    @SerializedName("freetext_question") PictureChallengeQuestion question;

    PictureChallenge() {
        // Mandatory empty constructor
        super();
        this.type = ChallengeType.PICTURE;
    }

    protected PictureChallenge(Parcel in) {
        super(in);
        if (in.readByte() == 0x1) {
            question = in.readParcelable(PictureChallenge.class.getClassLoader());
        } else {
            question = null;
        }
    }

    public PictureChallengeQuestion getQuestion() {
        return question;
    }

    public void setQuestion(PictureChallengeQuestion question) {
        this.question = question;
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
}
