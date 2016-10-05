package com.rr.rgem.gem.answers;

import android.content.Context;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.GoalActivity;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.controllers.common.JSONState;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;

import java.util.Map;

/**
 * Created by chris on 9/27/2016.
 */
public class GoalAnswers implements AnswerInterface
{
    private GoalActivity activity;
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

    public JSONState getState(){
        return this.state;
    }
    public void setState(JSONState state){
        this.state = state;
    }
    public JSONController getController(){
        return state.getController();
    }

    private GoalActivity getActivity() {
        return activity;
    }

    public String getName() {
        return name;
    }

    public void setActivity(GoalActivity activity) {
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
    public void save(String json){
        Persisted persisted = new Persisted(this.getActivity());
        persisted.saveConvState(getName(),json);
    }
    public void load(Context context){

    }
}
