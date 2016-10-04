package com.rr.rgem.gem.answers;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.GoalActivity;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.controllers.Validation;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.controllers.common.JSONState;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by chris on 9/27/2016.
 */
public class GoalsAnswers implements AnswerInterface
{

    private GoalActivity activity;
    private JSONState state;
    private final String name = "goals";
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
    private void checkSavingsGoals(ConversationNode.AnswerNode answer, String response){
            if(answer.getName().equalsIgnoreCase("hasSaved") || answer.getName().equalsIgnoreCase("goalDate")){

                Map<String, String> responses = getController().getResponseMap();
                Map<String, String> vars = getController().getVars();
                if(answer.getName().equals("goalDate")){
                    responses.put(answer.getName(),response);
                }
                Date goalDate = new Date();

                Double weeklySaveAmount = 0.0;
                Double goalAmount = 0.0;

                if(responses.containsKey("goalDate")) {
                    goalDate = Validation.convertDate(responses.get("goalDate"));
                    DateTime first = new DateTime();
                    DateTime last = new DateTime(goalDate);
                    int weeks = Weeks.weeksBetween(first, last).getWeeks();
                    goalAmount = Double.parseDouble(responses.get("goalAmount"));
                    weeklySaveAmount = goalAmount/ weeks;

                }else{
                    weeklySaveAmount = Double.parseDouble(responses.get("weeklySaveAmount"));
                    goalAmount = Double.parseDouble(responses.get("goalAmount"));
                    Double weeks = goalAmount/weeklySaveAmount;
                    DateTime calced = new DateTime();
                    calced.plus(Weeks.weeks(weeks.intValue()));
                    goalDate = calced.toDate();
                }
                DateTime first = new DateTime();
                DateTime last = new DateTime(goalDate);
                NumberFormat formatter = new DecimalFormat("#0.00");
                vars.put("weeklyGoal", formatter.format(weeklySaveAmount));
                vars.put("savingsGoal", formatter.format(goalAmount));
                int weeks = Weeks.weeksBetween(first, last).getWeeks();
                vars.put("totalWeeks", new Integer(weeks).toString());
            }
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
        checkSavingsGoals(answer,response);
        return true;
    }

    public boolean dateAnswer(ConversationNode.AnswerNode answer, String response){
        checkSavingsGoals(answer,response);
        return true;
    }

    public boolean passwordAnswer(ConversationNode.AnswerNode answer, String response){

        return true;
    }

    public void save(String json){
        //Persisted persisted = new Persisted(this.getActivity());
        //persisted.saveConvState(getName(),json);
    }
    public void load(){

    }
}
