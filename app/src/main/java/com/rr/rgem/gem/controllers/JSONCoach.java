package com.rr.rgem.gem.controllers;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.Challenges;
import com.rr.rgem.gem.models.Question;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by chris on 9/14/2016.
 */
public class JSONCoach {
    private final Challenges challenges ;
    private final Map<Long, Answer> answerMap = new HashMap<Long,Answer>();

    private int resource;
    private State state = State.Initiated;
    private long questionId = 0;
    private int challengeTracker = 0;
    private int questionTracker = 0;
    private Challenge challenge;
    private Question question;
    private String PasswordOne;
    private String PasswordTwo;

    private enum State
    {
        Initiated, Correct, Incorrect, Complete, Waiting
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

    public JSONCoach(Context context,int resource){
        Gson gson = new Gson();
        this.resource = resource;
        challenges = gson.fromJson(loadJsonFromResources(context),Challenges.class);

    }

    public JSONCoach(String json){
        Gson gson = new Gson();
        challenges = gson.fromJson(json,Challenges.class);

    }

    private void sendChallenges(final ChallengeActivity challengeActivity, final LeftRightConversation conversationView, CharSequence toastMessage)
    {
        if (challenges == null)
        {
            return;
        }

        if(toastMessage != null)
        {
            Utils.toast(challengeActivity, toastMessage);
        }

        challenge = challenges.getChallenges().get(challengeTracker);
        question = challenge.getQuestions().get(questionTracker);

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

            case Correct:
            {
                if (questionTracker < challenge.getQuestions().size() - 1)
                {
                    ++questionTracker;
                    question = challenge.getQuestions().get(questionTracker);
                }
                else
                {
                    if (challengeTracker < challenges.getChallenges().size() - 1)
                    {
                        ++challengeTracker;
                        challenge = challenges.getChallenges().get(challengeTracker);
                        questionTracker = 0;
                        question = challenge.getQuestions().get(questionTracker);

                    }
                    else
                    {
                        state = State.Complete;
                        sendChallenges(challengeActivity, conversationView, "Challenge Done");
                        break;
                    }
                }

                displayQuestion(challengeActivity, conversationView, question, questionId);
                ++questionId;
                state = State.Waiting;
                break;
            }

            case Complete:
            {

                if (challengeTracker == challenges.getChallenges().size() - 1 && questionTracker == challenge.getQuestions().size() - 1)
                {

                    //Utils.toast(challengeActivity,"Challenge Done");
                    challengeActivity.goToMain();

                }

                break;
            }

            case Initiated:
            {
                state = State.Correct;
                displayQuestion(challengeActivity, conversationView, question, questionId);
                ++questionId;
                break;
            }
        }
    }

    private void displayQuestion(final ChallengeActivity challengeActivity, final LeftRightConversation conversationView, Question question, long questionId)
    {
        Iterator<Answer> answers = question.getAnswers().iterator();


        while (answers.hasNext()) {
            final Answer answer = answers.next();
            if (answer.getType() == null) {
                continue;
            }
            this.answerMap.put(questionId, answer);
            if (answer.getType().equalsIgnoreCase("text")) {
                conversationView.addTextInputQuestion(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (v.getText() != "" && Validation.isAlphaNumeric(v.getText().toString())) {
                                state = State.Correct;
                                sendChallenges(challengeActivity, conversationView, "You answered: " + v.getText());
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer. Please enter a valid answer.");
                                return false;
                            }
                        }
                        return false;
                    }
                });
            } else if (answer.getType().equalsIgnoreCase("mobile")) {
                conversationView.addTextInputQuestion(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (v.getText() != "" && Validation.isValidMobile(v.getText().toString())) {
                                state = State.Correct;

                                sendChallenges(challengeActivity, conversationView, "You answered: " + v.getText());
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer. Please enter a valid answer.");
                                return false;
                            }
                        }
                        return false;
                    }
                });
            } else if (answer.getType().equalsIgnoreCase("currency")) {
                conversationView.addTextInputQuestion(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (v.getText() != "" &&  Validation.isValidCurrency(v.getText().toString())) {
                                state = State.Correct;

                                sendChallenges(challengeActivity, conversationView, "You answered: " + v.getText());
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer. Please enter a valid answer.");
                                return false;
                            }
                        }
                        return false;
                    }
                });
            } else if (answer.getType().equalsIgnoreCase("choice")) {
                Map<String, View.OnClickListener> listeners = new HashMap<String, View.OnClickListener>();
                Iterator<String> values = answer.getValues().iterator();
                while (values.hasNext()) {
                    String choice = values.next();
                    listeners.put(choice, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            state = State.Correct;
                            sendChallenges(challengeActivity, conversationView, "You selected: " + ((Button) v).getText());

                            for (int j = 0; j < ((ViewGroup) v.getParent()).getChildCount(); ++j) {
                                ((ViewGroup) v.getParent()).getChildAt(j).setEnabled(false);
                            }
                        }
                    });
                }

                if (!listeners.isEmpty()) {
                    conversationView.addMultipleChoiceQuestion(questionId, question.getText(), listeners);
                }
            } else if (answer.getType().equalsIgnoreCase("date")) {
                conversationView.addTextInputQuestion(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (v.getText() != "" && Validation.isValidDate(v.getText().toString())) {
                                state = State.Correct;
                                sendChallenges(challengeActivity, conversationView, "You gave the date: " + v.getText());
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer. Please enter a date in the format DD-MM-YYYY.");
                                return false;
                            }
                        }
                        return false;
                    }
                });
            } else if (answer.getType().equalsIgnoreCase("password")) {
                conversationView.addPasswordMessage(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (v.getText() != "" && Validation.isValidPassword(v.getText().toString())) {
                                PasswordOne = v.getText().toString();
                                state = State.Correct;
                                sendChallenges(challengeActivity, conversationView, "Saved");
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer. Please enter 8 or more characters(letters/numbers/special characters(- . _ ` ~ @ # $ & *)).");
                                return false;
                            }
                        }
                        return false;
                    }
                }, "Type password here...");

            } else if (answer.getType().equalsIgnoreCase("passwordTwo")) {
                conversationView.addPasswordMessage(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            PasswordTwo = v.getText().toString();
                            System.out.println("password1111::!!!!!!!!!!!!!!" + PasswordOne);
                            System.out.println("password22222::!!!!!!!!!!!!!!" + v.getText().toString());

                            if (v.getText() != "" && Validation.areMatching(PasswordOne,PasswordTwo)) {
                                state = State.Correct;
                                sendChallenges(challengeActivity, conversationView, "Saved");
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer.Does not match entered password.");
                                return false;
                            }
                        }
                        return false;
                    }
                }, "Type password here...");

            }else if (answer.getType().equalsIgnoreCase("initials")) {
                conversationView.addTextInputQuestion(questionId, question.getText(), new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (v.getText() != "" && Validation.isAlphabetic(v.getText().toString())) {
                                state = State.Correct;
                                sendChallenges(challengeActivity, conversationView, "You answered: " + v.getText());
                                v.setGravity(Gravity.RIGHT);
                                v.setEnabled(false);
                                return true;
                            } else {
                                state = State.Incorrect;
                                sendChallenges(challengeActivity, conversationView, "Invalid answer. Please enter a valid answer.");
                                return false;
                            }
                        }
                        return false;
                    }
                });
            }

        }
        conversationView.scroll();
    }

}
