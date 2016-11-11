package com.nike.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.nike.dooit.models.Question;

import java.util.ArrayList;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public class FreeformChallenge extends BaseChallenge {

    FreeformChallenge() {
        // Mandatory empty constructor
    }

    protected FreeformChallenge(Parcel in) {
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
