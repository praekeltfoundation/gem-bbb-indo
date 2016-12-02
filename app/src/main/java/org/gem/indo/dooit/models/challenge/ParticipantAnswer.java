package org.gem.indo.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by herman on 2016/11/05.
 */

public class ParticipantAnswer extends BaseParticipantAnswer implements Serializable {

    /*************
     * Variables *
     *************/

    @SerializedName("selected_option")
    private long selectedOption;


    /****************
     * Constructors *
     ****************/

    public ParticipantAnswer() {
        // Mandatory empty constructor
        super();
    }

    public ParticipantAnswer(long question, long selectedOption) {
        super();
        this.question = question;
        this.selectedOption = selectedOption;
        this.dateAnswered = new DateTime();
    }

    public ParticipantAnswer(long user, long challenge, long question, long selectedOption) {
        super();
        this.user = user;
        this.challenge = challenge;
        this.question = question;
        this.selectedOption = selectedOption;
        this.dateAnswered = new DateTime();
    }


    /***********
     * Getters *
     ***********/

    public long getSelectedOption() {
        return selectedOption;
    }


    /***********
     * Setters *
     ***********/

    public void setSelectedOption(long selectedOption) {
        this.selectedOption = selectedOption;
    }


    /************************
     * Parcelable interface *
     ************************/

    protected ParticipantAnswer(Parcel in) {
        super(in);
        selectedOption = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(selectedOption);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ParticipantAnswer> CREATOR = new Parcelable.Creator<ParticipantAnswer>() {
        @Override
        public ParticipantAnswer createFromParcel(Parcel in) {
            return new ParticipantAnswer(in);
        }

        @Override
        public ParticipantAnswer[] newArray(int size) {
            return new ParticipantAnswer[size];
        }
    };
}
