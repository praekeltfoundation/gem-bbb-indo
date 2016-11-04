package com.rr.rgem.gem.controllers;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rr.rgem.gem.R;
import com.rr.rgem.gem.controllers.common.JSONController;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.models.Question;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 9/14/2016.
 */
public class JSONConversation {
    ConversationNode[] conversation;

    int resource;
    Pattern varStrPattern = Pattern.compile("[$](?:([$])|[(]([a-zA-Z][a-zA-Z_]*)[)])");
    Map<String, ConversationNode> nodeMap = new HashMap<String, ConversationNode>();
    Map<String, String> responseMap = new HashMap<String, String>();
    Map<String, String> varMap = new HashMap<String, String>();
    Map<String, ConvoCallback> extFnMap = new HashMap<String, ConvoCallback>();
    ConvoCallback doneCallback;
    State state = State.Initiated;
    ConversationNode current;
    int questionId;
    String PasswordOne;
    String PasswordTwo;
    ImageView currentImage;

    private enum State
    {
        Initiated, Correct, Incorrect, Complete, Waiting
    }

    String loadJsonFromResources(Context context){
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

    public JSONConversation(Context context, int resource){
        Gson gson = new Gson();
        this.resource = resource;
        conversation = gson.fromJson(loadJsonFromResources(context),ConversationNode[].class);

    }

    public JSONConversation(String json){
        Gson gson = new Gson();
        conversation = gson.fromJson(json,ConversationNode[].class);
    }

    public void sendChallenges(final AppCompatActivity conversationActivity, final LeftRightConversation conversationView, CharSequence toastMessage)
    {
        if (conversation == null)
        {
            return;
        }

        if(toastMessage != null)
        {
            Utils.toast(conversationActivity, toastMessage);
        }

        if (nodeMap.size() < 1) {
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
                current = conversation[0];
                questionId = 1;
                state = State.Correct;
            }

            case Correct:
            {
                if (current == null) {
                    state = State.Complete;
                    break;
                }
                Question question = new Question(this.parseStringVars(current.text));
                displayQuestion(conversationActivity, conversationView, question, questionId++);
                state = State.Waiting;
                break;
            }

            case Complete:
            {
                varMap.put("questionId", String.format("%d", questionId++));
               /* if (doneCallback != null) {
                    doneCallback.callback(varMap, responseMap);
                }*/

                        doneCallback.callback(varMap, responseMap);
                break;
            }
        }
    }

    private void displayQuestion(final AppCompatActivity conversationActivity, final LeftRightConversation conversationView, Question question, long questionId)
    {
        if (current.type == ConversationNode.NodeType.text) {
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addTextInputQuestion(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    if (v.getText() != "" && Validation.isAlphaNumeric(v.getText().toString()))
                    {
                        state = State.Correct;
                        responseMap.put(answer.name, v.getText().toString());
                        current = nodeMap.get(getNextNode(answer.next));
                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.youAnswered) + v.getText());
                        v.setGravity(Gravity.RIGHT);
                        v.setEnabled(false);
                        return true;
                    }
                    else if (current.error != null && !current.error.equals(""))
                    {
                        state = State.Correct;
                        current = nodeMap.get(getNextNode(current.error));
                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                        v.setGravity(Gravity.RIGHT);
                        v.setEnabled(false);
                        return false;
                    }
                    else
                    {
                        state = State.Incorrect;

                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                        return false;
                    }
                }
                return false;
                }
            });
        } else if(current.type == ConversationNode.NodeType.currency){
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addTextInputQuestion(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    if (v.getText() != "" && Validation.isValidCurrency(v.getText().toString()))
                    {
                        state = State.Correct;
                        responseMap.put(answer.name, v.getText().toString());
                        current = nodeMap.get(getNextNode(answer.next));
                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.youAnswered) + v.getText());
                        v.setGravity(Gravity.RIGHT);
                        v.setEnabled(false);
                        return true;
                    }
                    else if (current.error != null && !current.error.equals(""))
                    {
                        state = State.Correct;
                        current = nodeMap.get(getNextNode(current.error));
                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                        v.setGravity(Gravity.RIGHT);
                        v.setEnabled(false);
                        return false;
                    }
                    else
                    {
                        state = State.Incorrect;

                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                        return false;
                    }
                }
                return false;
                }
            });
        } else if (current.type == ConversationNode.NodeType.choice) {
            Map<String,View.OnClickListener> listeners = new HashMap<String,View.OnClickListener>();
            for (final ConversationNode.AnswerNode choice: current.answers){
                listeners.put(choice.value, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        state = State.Correct;
                        responseMap.put(choice.name, choice.value);
                        current = nodeMap.get(getNextNode(choice.next));
                        sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.youSelected) + ((Button) v).getText());

                        for (int j = 0; j < ((ViewGroup) v.getParent()).getChildCount(); ++j)
                        {
                            ((ViewGroup) v.getParent()).getChildAt(j).setEnabled(false);
                        }
                    }
                });
            }

            if(!listeners.isEmpty()) {
                conversationView.addMultipleChoiceQuestion(questionId, question.getText(), listeners);
            }
        } else if (current.type == ConversationNode.NodeType.date) {
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addTextInputQuestion(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if (v.getText() != "" && Validation.isValidDate(v.getText().toString()))
                        {
                            state = State.Correct;
                            responseMap.put(answer.name, v.getText().toString());
                            current = nodeMap.get(getNextNode(answer.next));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.youAnswered) + v.getText());
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return true;
                        }
                        else if (current.error != null && !current.error.equals(""))
                        {
                            state = State.Correct;
                            current = nodeMap.get(getNextNode(current.error));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return false;
                        }
                        else
                        {
                            state = State.Incorrect;

                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            return false;
                        }
                    }
                    return false;
                }
            });
        } else if (current.type == ConversationNode.NodeType.info) {
            if (current != null && current.text != null && !current.text.equals("")) {
                state = State.Correct;
                Message msg = new Message(questionId,"2016",true, Message.ResponseType.FreeForm, null);
                msg.setTitle(parseStringVars(current.text));
                conversationView.addFreeFormPlain(msg);
                String nextStr = getNextNode(current.next);
                if (nextStr != null && !nextStr.equals("") && nodeMap.containsKey(nextStr)) {
                    current = nodeMap.get(nextStr);
                } else {
                    current = null;
                    state = State.Complete;
                }
            } else {
                current = null;
                state = State.Complete;
            }
            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.info));
        }else if (current.type == ConversationNode.NodeType.initials) {
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addTextInputQuestion(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if (v.getText() != "" && Validation.isAlphabetic(v.getText().toString()))
                        {
                            state = State.Correct;
                            responseMap.put(answer.name, v.getText().toString());
                            current = nodeMap.get(getNextNode(answer.next));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.youAnswered) + v.getText());
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return true;
                        }
                        else if (current.error != null && !current.error.equals(""))
                        {
                            state = State.Correct;
                            current = nodeMap.get(getNextNode(current.error));
                            sendChallenges(conversationActivity, conversationView,conversationActivity.getResources().getString(R.string.inValidAnswer));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return false;
                        }
                        else
                        {
                            state = State.Incorrect;

                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            return false;
                        }
                    }
                    return false;
                }
            });
        }else if (current.type == ConversationNode.NodeType.mobile) {
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addTextInputQuestion(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if (v.getText() != "" && Validation.isValidMobile(v.getText().toString()))
                        {
                            state = State.Correct;
                            responseMap.put(answer.name, v.getText().toString());
                            current = nodeMap.get(getNextNode(answer.next));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.youAnswered) + v.getText());
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return true;
                        }
                        else if (current.error != null && !current.error.equals(""))
                        {
                            state = State.Correct;
                            current = nodeMap.get(getNextNode(current.error));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return false;
                        }
                        else
                        {
                            state = State.Incorrect;

                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            return false;
                        }
                    }
                    return false;
                }
            });
        }else if (current.type == ConversationNode.NodeType.password) {
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addPasswordMessage(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if (v.getText() != "" && Validation.isValidPassword(v.getText().toString()))
                        {
                            PasswordOne=v.getText().toString();
                            state = State.Correct;
                            responseMap.put(answer.name, v.getText().toString());
                            current = nodeMap.get(getNextNode(answer.next));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.saved));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return true;
                        }
                        else if (current.error != null && !current.error.equals(""))
                        {
                            state = State.Correct;
                            current = nodeMap.get(getNextNode(current.error));
                            sendChallenges(conversationActivity, conversationView,conversationActivity.getResources().getString(R.string.inValidAnswer));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return false;
                        }
                        else
                        {
                            state = State.Incorrect;

                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            return false;
                        }
                    }
                    return false;
                }
            },conversationActivity.getResources().getString(R.string.typePasswordHere));
        } else if (current.type == ConversationNode.NodeType.passwordTwo) {
            final ConversationNode.AnswerNode answer = current.answers[0];
            conversationView.addPasswordMessage(questionId, parseStringVars(current.text), new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        PasswordTwo = v.getText().toString();
                        System.out.println("password1111::!!!!!!!!!!!!!!" + PasswordOne);
                        System.out.println("password22222::!!!!!!!!!!!!!!" + v.getText().toString());
                        if (v.getText() != "" &&  Validation.areMatching(PasswordOne,PasswordTwo))
                        {
                            state = State.Correct;
                            responseMap.put(answer.name, v.getText().toString());
                            current = nodeMap.get(getNextNode(answer.next));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.saved));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return true;
                        }
                        else if (current.error != null && !current.error.equals(""))
                        {
                            state = State.Correct;
                            current = nodeMap.get(getNextNode(current.error));
                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            v.setGravity(Gravity.RIGHT);
                            v.setEnabled(false);
                            return false;
                        }
                        else
                        {
                            state = State.Incorrect;

                            sendChallenges(conversationActivity, conversationView, conversationActivity.getResources().getString(R.string.inValidAnswer));
                            return false;
                        }
                    }
                    return false;
                }
            },conversationActivity.getResources().getString(R.string.typePasswordHere));
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
            state = State.Incorrect;
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

    private String parseStringVars(String str)
    {
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
}
