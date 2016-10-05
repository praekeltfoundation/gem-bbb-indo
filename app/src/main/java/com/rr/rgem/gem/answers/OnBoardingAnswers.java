package com.rr.rgem.gem.answers;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rr.rgem.gem.OnBoardingActivity;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.controllers.common.JSONState;
import com.rr.rgem.gem.controllers.Validation;
import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.ConversationNode;

import java.util.Map;

/**
 * Created by chris on 9/26/2016.
 */
public class OnBoardingAnswers implements AnswerInterface {

    private String PasswordOne;
    private String PasswordTwo;
    private OnBoardingActivity activity;
    private final String name = "onboarding";
    private JSONState state;

    public JSONState getState(){
        return this.state;
    }
    public void setState(JSONState state){
        this.state = state;
    }
    public JSONController getController(){
        return state.getController();
    }

    public String getName(){
        return this.name;
    }
    public OnBoardingActivity getActivity() {
        return activity;
    }

    public void setActivity(OnBoardingActivity activity) {
        this.activity = activity;
    }

    public void start(){
        this.getState().sendChallenges(activity,null);
    }
    public String complete(Map<String, String> vars, Map<String, String> responses){
        return  "";
    }
    public boolean textAnswer(ConversationNode.AnswerNode answer, String response){
        return true;

    }
    public boolean mobileAnswer(ConversationNode.AnswerNode answer, String response){
        return true;

    }
    public boolean currencyAnswer(ConversationNode.AnswerNode answer, String response){
        return true;
    }

    public boolean choiceAnswer(ConversationNode.AnswerNode answer, String response){

        return true;
    }
    public boolean dateAnswer(ConversationNode.AnswerNode answer, String response){
        return true;

    }
    public boolean passwordAnswer(ConversationNode.AnswerNode answer, String response){

        return true;

    }
    private boolean answerPassword2(Answer answer, TextView v){
        PasswordTwo = v.getText().toString();
        System.out.println("password1111::!!!!!!!!!!!!!!" + PasswordOne);
        System.out.println("password22222::!!!!!!!!!!!!!!" + v.getText().toString());

        if (v.getText() != "" && Validation.areMatching(PasswordOne,PasswordTwo)) {
            getState().setState(JSONState.State.Correct);
            getState().sendChallenges(activity, "Saved");
            v.setGravity(Gravity.END);
            v.setEnabled(false);
            return true;
        } else {
            getState().setState(JSONState.State.Incorrect);
            getState().sendChallenges(activity, "Invalid answer.Does not match entered password.");
            return false;
        }
    }
    public void save(String json){
        Persisted persisted = new Persisted(this.getActivity());
        persisted.saveConvState(getName(),json);
    }
    public void load(Context context){

    }
}
