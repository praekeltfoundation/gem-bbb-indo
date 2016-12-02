package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by Rudolph Jacobs on 2016-11-14.
 */

public class ParticipantFreeformAnswer extends BaseParticipantAnswer {

    /*************
     * Variables *
     *************/

    private String text;


    /****************
     * Constructors *
     ****************/

    public ParticipantFreeformAnswer() {
        // Mandatory empty constructor
        super();
    }

    public ParticipantFreeformAnswer(long user, long challenge, long question, String text) {
        super();
        this.user = user;
        this.challenge = challenge;
        this.question = question;
        this.text = text;
        this.dateAnswered = new DateTime();
    }


    /***********
     * Getters *
     ***********/

    public String getText() {
        return text;
    }


    /***********
     * Setters *
     ***********/

    public void setText(String text) {
        this.text = text;
    }


    /************************
     * Parcelable interface *
     ************************/

    protected ParticipantFreeformAnswer(Parcel in) {
        super(in);
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(text);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ParticipantFreeformAnswer> CREATOR = new Parcelable.Creator<ParticipantFreeformAnswer>() {
        @Override
        public ParticipantFreeformAnswer createFromParcel(Parcel in) {
            return new ParticipantFreeformAnswer(in);
        }

        @Override
        public ParticipantFreeformAnswer[] newArray(int size) {
            return new ParticipantFreeformAnswer[size];
        }
    };
}
