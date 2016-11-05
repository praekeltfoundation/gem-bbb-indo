package com.nike.dooit.models;

import org.joda.time.DateTime;

/**
 * Created by herman on 2016/11/05.
 */

public class ParticipantAnswer {

    private int question;
    private int selected_option;
    private DateTime date_answered = new DateTime();

    public ParticipantAnswer(int question, int selected_option) {
        this.question = question;
        this.selected_option = selected_option;
        this.date_answered = new DateTime();
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public int getSelected_option() {
        return selected_option;
    }

    public void setSelected_option(int selected_option) {
        this.selected_option = selected_option;
    }

    public DateTime getDate_answered() {
        return date_answered;
    }

    public void setDate_answered(DateTime date_answered) {
        this.date_answered = date_answered;
    }
}
