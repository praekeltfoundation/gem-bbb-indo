package com.rr.rgem.gem.controllers.common;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.answers.AnswerInterface;
import com.rr.rgem.gem.controllers.Validation;
import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.models.Question;
import com.rr.rgem.gem.views.LeftRightConversation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 9/26/2016.
 */
public class JSONController {

    private Map<Long, Answer> answerMap = new HashMap<Long,Answer>();
    final private int resource;
    private ConversationNode[] conversation;
    private ConversationNode current;
    private JSONState state;
    private long questionId = 0;
    private int challengeTracker = 0;
    private int questionTracker = 0;
    private Challenge challenge;
    private Question question;
    private final AnswerInterface answerProcess;
    private final Map<String, String> responseMap = new HashMap<String, String>();
    private Map<String, String> varMap = new HashMap<String, String>();
    private Map<String, ConvoCallback> extFnMap = new HashMap<String, ConvoCallback>();
    private ConvoCallback doneCallback;

    public AnswerInterface getAnswerProcess() {
        return answerProcess;
    }

    public Map<String, String> getResponseMap() {
        return responseMap;
    }

    public ConversationNode getCurrent() {
        return current;
    }

    public void setCurrent(ConversationNode current) {
        this.current = current;
    }

    public ConversationNode[] getConversation() {
        return conversation;
    }

    public void setConversation(ConversationNode[] conversation) {
        this.conversation = conversation;
    }

    private JSONState getState() {
        return state;
    }

    public void setState(JSONState state) {
        this.state = state;
    }

    public String toJson(){
        Gson g = new Gson();
        return g.toJson(this.conversation);
    }
    private void saveJson(){
        String generated = toJson();
        if(answerProcess != null)
            answerProcess.save(generated);
    }
    private String loadJsonFromResources(Context context){
        InputStream is = context.getResources().openRawResource(resource);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (Exception e){
        } finally {
            try {
                is.close();
            }catch(Exception e){
            }
        }

        return writer.toString();
    }
    JSONController(Context context, int resource,AnswerInterface answerProcess){
        Gson gson = new Gson();
        this.resource = resource;
        this.answerProcess = answerProcess;
        Persisted persisted = new Persisted(context);
        String saved = persisted.loadConvState(answerProcess.getName());
        if(Validation.isEmpty(saved)) {
            conversation = gson.fromJson(loadJsonFromResources(context),ConversationNode[].class);
        }else{
            conversation = gson.fromJson(saved,ConversationNode[].class);
        }
    }
    private void sendErrorAnswer(AppCompatActivity activity,TextView v){
        if (current.error != null && !current.error.equals(""))
        {
            getState().setState(JSONState.State.Correct);
            current = getState().getNodeMap().get(getNextNode(current.error));
            getState().sendChallenges(activity, "Invalid answer. Please enter a valid answer.");
            v.setGravity(Gravity.END);
            v.setEnabled(false);
        }else{
            getState().setState(JSONState.State.Incorrect);
            getState().sendChallenges(activity, "Invalid answer. Please enter a valid answer.");

        }

    }
    private void sendSuccess(AppCompatActivity activity,ConversationNode.AnswerNode answer,TextView v){
        current = getState().getNodeMap().get(getNextNode(answer.next));
        responseMap.put(answer.name, v.getText().toString());
        v.setGravity(Gravity.END);
        v.setEnabled(false);
    }
    private boolean textAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer, TextView v){

        String response = v.getText().toString();
        if (
                !Validation.isEmpty(response) && Validation.isAlphaNumeric(response)
        &&  answerProcess.textAnswer(answer,v.getText().toString())
        ){
            sendSuccess(activity,answer,v);
            return true;
        }else{
            sendErrorAnswer(activity,v);
        }

        return false;
    }
    private boolean mobileAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();
        if (
                !Validation.isEmpty(response) && Validation.isValidMobile(response)
        &&  answerProcess.mobileAnswer(answer,v.getText().toString())
        ){
            sendSuccess(activity,answer,v);
            return true;
        }else{
            sendErrorAnswer(activity,v);
        }
        return false;
    }
    private boolean currencyAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer,TextView v){

        String response = v.getText().toString();
        if (
                !Validation.isEmpty(response) && Validation.isValidCurrency(response)
            && answerProcess.currencyAnswer(answer,response))
        {
            sendSuccess(activity,answer,v);
            return true;
        } else {
            sendErrorAnswer(activity, v);
        }
        return false;
    }
    public boolean choiceAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer,View v){
        String response = ((Button) v).getText().toString();
        current = getState().getNodeMap().get(getNextNode(answer.next));
        answerProcess.choiceAnswer(answer,response);
        responseMap.put(answer.name, response);
        for (int j = 0; j < ((ViewGroup) v.getParent()).getChildCount(); ++j) {
            ((ViewGroup) v.getParent()).getChildAt(j).setEnabled(false);
        }
        return true;
    }
    private boolean dateAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();

        if(!Validation.isEmpty(response) && Validation.isValidDate(response) && answerProcess.dateAnswer(answer,response)){
            sendSuccess(activity,answer,v);
            return true;
        }else {
            sendErrorAnswer(activity, v);
        }
        return false;
    }
    private boolean numberAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();

        if(!Validation.isEmpty(response) && Validation.isValidNumber(response) && answerProcess.textAnswer(answer,response)){
            sendSuccess(activity,answer,v);
            return true;
        }else {
            sendErrorAnswer(activity, v);
        }
        return false;
    }
    private boolean passwordAnswer(AppCompatActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();
        if(!Validation.isEmpty(response) && Validation.isValidDate(response) && answerProcess.passwordAnswer(answer,response)){
            sendSuccess(activity,answer,v);
            return true;
        }
        return false;

    }
    public void saveState(){
        String generated = toJson();
    }

    private boolean anyTextAnswer(final AppCompatActivity activity,ConversationNode.NodeType type,ConversationNode.AnswerNode answer,TextView v){
        if(type == ConversationNode.NodeType.currency){
            return currencyAnswer(activity,answer,v);
        }else if(type == ConversationNode.NodeType.date){
            return dateAnswer(activity,answer,v);
        }else if(type == ConversationNode.NodeType.info){
            return textAnswer(activity,answer,v);
        }else if(type == ConversationNode.NodeType.number){
            return numberAnswer(activity,answer,v);
        }else if(type == ConversationNode.NodeType.text) {
            return textAnswer(activity,answer,v);
        }else if(type ==     ConversationNode.NodeType.mobile){
            return mobileAnswer(activity,answer,v);
        }else if(type == ConversationNode.NodeType.password){
            return passwordAnswer(activity,answer,v);
        }
        return false;
    }
    public void displayQuestion(final AppCompatActivity activity, final LeftRightConversation conversationView, Question question, long questionId) {

        final ConversationNode.AnswerNode answer = current.answers[0];
        //this.answerMap.put(questionId, answer);

        if (current.type == ConversationNode.NodeType.choice) {
            Map<String, View.OnClickListener> listeners = new HashMap<String, View.OnClickListener>();
            for (final ConversationNode.AnswerNode choice: current.answers)
            {
                listeners.put(choice.value, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONController.this.getState().setState(JSONState.State.Correct);
                        responseMap.put(choice.name, choice.value);
                        current = JSONController.this.getState().getNodeMap().get(getNextNode(choice.next));
                        JSONController.this.getState().sendChallenges(activity, "You selected: " + ((Button) v).getText());
                        choice.setResponse(choice.value);
                        JSONController.this.saveJson();
                        for (int j = 0; j < ((ViewGroup) v.getParent()).getChildCount(); ++j)
                        {
                            ((ViewGroup) v.getParent()).getChildAt(j).setEnabled(false);
                        }
                    }
                });
            }


            if (!listeners.isEmpty()) {
                conversationView.addMultipleChoiceQuestion(questionId, question.getText(), listeners);
                for (final ConversationNode.AnswerNode choice: current.answers) {
                    if (!Validation.isEmpty(choice.getResponse())) {
                        JSONController.this.getState().setState(JSONState.State.Correct);
                        responseMap.put(choice.name, choice.value);
                        current = JSONController.this.getState().getNodeMap().get(getNextNode(choice.next));
                        JSONController.this.getState().sendChallenges(activity, "You selected: " + choice.getResponse());
                        choice.setResponse(choice.value);
                        JSONController.this.saveJson();
                        break; /// TODO: at first choice recorded
                    }
                }
            }

        } else if (current.type == ConversationNode.NodeType.password) {
            conversationView.addPasswordMessage(questionId, question.getText(), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        if(passwordAnswer(activity,answer,v)){
                            JSONController.this.saveJson();
                        }
                        return true;
                    }
                    return false;
                }
            }, "Type password here...");
            if(!Validation.isEmpty(answer.getResponse())){
                TextView v = (TextView)conversationView.getViews().get(questionId);
                v.setText(answer.getResponse());
                passwordAnswer(activity,answer,v);
            }
        }else {

            conversationView.addTextInputQuestion(questionId, question.getText(), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if(anyTextAnswer(activity,current.type ,answer,v)){
                            JSONController.this.saveJson();
                        }

                        return true;
                    }
                    return false;
                }
            });
            if(!Validation.isEmpty(answer.getResponse())){
                TextView v = (TextView)conversationView.getViews().get(questionId);
                v.setText(answer.getResponse());
                anyTextAnswer(activity,current.type,answer,v);
            }


        }

        conversationView.scroll();
    }
    /* external function logic */
    public Map<String, ConvoCallback> getFnMap()
    {
        return this.extFnMap;
    }

    public void setFnMap(Map<String, ConvoCallback> fnMap)
    {
        this.extFnMap = fnMap;
    }

    public void setDoneCallback(ConvoCallback callback)
    {
        this.doneCallback = callback;
    }

    public Map<String, String> getResponses()
    {
        return this.responseMap;
    }

    public Map<String, String> getVars()
    {
        return this.varMap;
    }

    public void setVars(Map<String, String> vars)
    {
        this.varMap = vars;
    }

    private String callExternalFunction(String fname)
    {
        if (fname == null || fname.equals("") && !extFnMap.containsKey(fname)) return null;
        try
        {
            return extFnMap.get(fname).callback(this.varMap, this.responseMap);
        }
        catch (Exception e)
        {
            getState().setState(JSONState.State.Incorrect);
        }
        return null;
    }

    private String getNextNode(String nextStr)
    {
        if (nextStr == null || nextStr.equals("")) return null;
        String result = nextStr;
        if (nextStr.indexOf('@') == 0) {
            String fname = nextStr.substring(1);
            result = callExternalFunction(fname);
        }
        return result;
    }
}
