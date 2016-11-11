package com.nike.dooit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nike.dooit.models.enums.ChallengeType;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Challenge implements Serializable, Parcelable {
    private String id;
    private String name;
    @SerializedName("activation_date")
    private DateTime activationDate;
    @SerializedName("deactivation_date")
    private DateTime deactivationDate;
    private ChallengeType type;
    private List<Question> questions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(DateTime activationDate) {
        this.activationDate = activationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public ChallengeType getType() {
        return type;
    }

    public void setType(ChallengeType type) {
        this.type = type;
    }

    public DateTime getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(DateTime deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    protected Challenge(Parcel in) {
        id = in.readString();
        name = in.readString();
        activationDate = (DateTime) in.readValue(DateTime.class.getClassLoader());
        deactivationDate = (DateTime) in.readValue(DateTime.class.getClassLoader());
        type = (ChallengeType) in.readValue(ChallengeType.class.getClassLoader());
        if (in.readByte() == 0x01) {
            questions = new ArrayList<Question>();
            in.readList(questions, Question.class.getClassLoader());
        } else {
            questions = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeValue(activationDate);
        dest.writeValue(deactivationDate);
        dest.writeValue(type);
        if (questions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(questions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Challenge> CREATOR = new Parcelable.Creator<Challenge>() {
        @Override
        public Challenge createFromParcel(Parcel in) {
            return new Challenge(in);
        }

        @Override
        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };
}