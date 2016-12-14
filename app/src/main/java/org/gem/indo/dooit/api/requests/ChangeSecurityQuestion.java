package org.gem.indo.dooit.api.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chris on 2016-11-22.
 */

public class ChangeSecurityQuestion {

    @SerializedName("new_answer")
    private String newAnswer;

    @SerializedName("new_question")
    private String newQuestion;

    public ChangeSecurityQuestion() {
    }

    public ChangeSecurityQuestion(String question, String answer){
        this.newAnswer = answer;
        this.newQuestion = question;
    }

    public String getNewAnswer() {
        return newAnswer;
    }

    public String getNewQuestion() {
        return newQuestion;
    }

    public void setNewAnswer(String newAnswer) {
        this.newAnswer = newAnswer;
    }

    public void setNewQuestion(String newQuestion) {
        this.newQuestion = newQuestion;
    }
}
