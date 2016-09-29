package com.rr.rgem.gem.answers;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.controllers.common.JSONState;
import com.rr.rgem.gem.controllers.Validation;
import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;

import java.util.Map;

/**
 * Created by chris on 9/27/2016.
 */
public class ChallengesAnswers implements AnswerInterface
{
    private ChallengeActivity activity;
    private JSONState state;
    private final String name = "challenges";
    private ConvoCallback endCall = null;
    public void setDoneCallback(ConvoCallback done){
        this.endCall = done;
    }
    public ConvoCallback getEndCall() {
        return endCall;
    }

    public void setEndCall(ConvoCallback endCall) {
        this.endCall = endCall;
    }

    private JSONState getState(){
        return this.state;
    }
    public void setState(JSONState state){
        this.state = state;
    }
    public JSONController getController(){
        return state.getController();
    }

    private ChallengeActivity getActivity() {
        return activity;
    }

    public String getName() {
        return name;
    }

    public void setActivity(ChallengeActivity activity) {
        this.activity = activity;
    }

    public void start(){
        this.getState().sendChallenges(activity,null);
    }

    public String complete(Map<String, String> vars, Map<String, String> responses){
        if(this.endCall!=null)
            this.endCall.callback(vars,responses);
        return  "";
    }

    public boolean textAnswer(ConversationNode.AnswerNode answer, String response){
        getState().setState(JSONState.State.Correct);
        getState().sendChallenges(activity, "You answered: " + response);
        answer.response = response;
        return true;
    }
    public boolean mobileAnswer(ConversationNode.AnswerNode answer, String response){
        getState().setState(JSONState.State.Correct);
        getState().sendChallenges(activity, "You answered: " + response);
        answer.response = response;
        return true;

    }
    public boolean currencyAnswer(ConversationNode.AnswerNode answer, String response){
        getState().setState(JSONState.State.Correct);
        getState().sendChallenges(activity, "You answered: " + response);
        answer.response = response;
        return true;
    }

    public boolean choiceAnswer(ConversationNode.AnswerNode answer, String response){
        getState().setState(JSONState.State.Correct);
        getState().sendChallenges(activity, "You selected: " + response);
        answer.response = response;
        return true;
    }
    public boolean dateAnswer(ConversationNode.AnswerNode answer, String response){

        getState().setState(JSONState.State.Correct);
        getState().sendChallenges(activity, "You gave the date: " + response);
        answer.response = response;
        return true;

    }
    public boolean passwordAnswer(ConversationNode.AnswerNode answer, String response){

        getState().setState(JSONState.State.Correct);
        getState().sendChallenges(activity, "Saved");
        answer.response = response;
        return true;

    }
    public void save(String json){
        Persisted persisted = new Persisted(this.getActivity());
        persisted.saveConvState(getName(),json);
    }
    public void load(){

    }
}
