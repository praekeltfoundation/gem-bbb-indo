package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public class FreeformChallengeQuestion extends BaseChallengeQuestion {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FreeformChallengeQuestion> CREATOR = new Parcelable.Creator<FreeformChallengeQuestion>() {
        @Override
        public FreeformChallengeQuestion createFromParcel(Parcel in) {
            return new FreeformChallengeQuestion(in);
        }

        @Override
        public FreeformChallengeQuestion[] newArray(int size) {
            return new FreeformChallengeQuestion[size];
        }
    };

    FreeformChallengeQuestion() {
        // Mandatory empty constructor
    }

    protected FreeformChallengeQuestion(Parcel in) {
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
