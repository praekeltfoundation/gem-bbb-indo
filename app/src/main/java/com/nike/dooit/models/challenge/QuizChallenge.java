package com.nike.dooit.models.challenge;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public class QuizChallenge extends BaseChallenge {
    List<QuizChallengeQuestion> questions = new ArrayList<>();

    QuizChallenge() {
        // Mandatory empty constructor
    }

    public List<QuizChallengeQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizChallengeQuestion> questions) {
        this.questions = questions;
    }

    protected QuizChallenge(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            questions = new ArrayList<QuizChallengeQuestion>();
            in.readList(questions, QuizChallengeQuestion.class.getClassLoader());
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
        super.writeToParcel(dest, flags);
        if (questions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(questions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QuizChallenge> CREATOR = new Parcelable.Creator<QuizChallenge>() {
        @Override
        public QuizChallenge createFromParcel(Parcel in) {
            return new QuizChallenge(in);
        }

        @Override
        public QuizChallenge[] newArray(int size) {
            return new QuizChallenge[size];
        }
    };
}
