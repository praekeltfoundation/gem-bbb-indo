package com.rr.rgem.gem.controllers.common;

import android.support.v7.app.AppCompatActivity;

import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.Challenges;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.Question;
import com.rr.rgem.gem.views.CoachConversation;
import com.rr.rgem.gem.views.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 9/26/2016.
 */
public class JSONState {
    public enum State
    {
        Initiated, Correct, Incorrect, Complete, Waiting
    }
    private State state = State.Initiated;
    private CoachConversation conversationView ;
    private Map<String, ConversationNode> nodeMap = new HashMap<String, ConversationNode>();
    Pattern varStrPattern = Pattern.compile("[$](?:([$])|[(]([a-zA-Z][a-zA-Z_]*)[)])");

    int challengeTracker = 0;
    int questionTracker = 0;
    long questionId = 1;
    private JSONController controller;

    public Map<String, ConversationNode> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, ConversationNode> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public void setController(JSONController controller){
        this.controller = controller;
    }

    public JSONController getController(){
        return  this.controller;
    }

    public void setConversationView(CoachConversation conversationView){
        this.conversationView = conversationView;
    }

    public CoachConversation getConversationView(){
        return conversationView;
    }

    private String parseStringVars(String str)
    {
        Map<String,String> varMap = getController().getVars();
        if (str == null || str.equals("")) return "";
        StringBuffer newStr = new StringBuffer();
        Matcher m = varStrPattern.matcher(str);
        while (m.find()) {
            String g1 = m.group(1);
            String g2 = m.group(2);
            if (m.group(1) != null) {
                m.appendReplacement(newStr, "\\$");
            } else if (varMap.containsKey(m.group(2))) {
                m.appendReplacement(newStr, varMap.get(m.group(2)));
            } else {
                m.appendReplacement(newStr, "");
            }
        }
        m.appendTail(newStr);
        return newStr.toString();
    }


    public void setState(State state){
        this.state = state;
    }
    public void sendChallenges(final AppCompatActivity activity, CharSequence toastMessage)
    {
        ConversationNode[] conversation = getController().getConversation();
        Map<String,String> varMap = getController().getVars();
        if (conversation == null)
        {
            return;
        }

        if(toastMessage != null)
        {
            Utils.toast(activity, toastMessage);
        }


        switch (state)
        {
            case Waiting:
            {
                break;
            }

            case Incorrect:
            {
                break;
            }

            case Initiated:
            {
                for (ConversationNode node: conversation) {
                    if (node.name != null && !node.name.equals("")) {
                        nodeMap.put(node.name, node);
                    }
                }
                if (nodeMap.size() < 1) {
                    state = State.Incorrect;
                    return;
                }
                getController().setCurrent(conversation[0]);
                questionId = 1;
                state = State.Correct;
            }

            case Correct:
            {
                if (getController().getCurrent()== null) {
                    state = State.Complete;
                    break;
                }
                Question question = new Question(this.parseStringVars(getController().getCurrent().text));
                getController().displayQuestion(activity, conversationView, question, questionId++);
                state = State.Waiting;
                break;
            }

            case Complete:
            {
                varMap.put("questionId", String.format("%d", questionId++));
               /* if (doneCallback != null) {
                    doneCallback.callback(varMap, responseMap);
                }*/
                getController().getAnswerProcess().complete(varMap,getController().getResponseMap());
                //doneCallback.callback(varMap, responseMap);
                break;
            }
        }
    }
}
