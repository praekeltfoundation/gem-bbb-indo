package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rudolph Jacobs on 2016-11-29.
 */

public class PictureChallengeQuestion extends BaseChallengeQuestion {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PictureChallengeQuestion> CREATOR = new Parcelable.Creator<PictureChallengeQuestion>() {
        @Override
        public PictureChallengeQuestion createFromParcel(Parcel in) {
            return new PictureChallengeQuestion(in);
        }

        @Override
        public PictureChallengeQuestion[] newArray(int size) {
            return new PictureChallengeQuestion[size];
        }
    };

    PictureChallengeQuestion() {
        // Mandatory empty constructor
    }

    protected PictureChallengeQuestion(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
