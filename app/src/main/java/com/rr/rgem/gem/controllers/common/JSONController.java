package com.rr.rgem.gem.controllers.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rr.rgem.gem.ApplicationActivity;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.R;
import com.rr.rgem.gem.answers.AnswerInterface;
import com.rr.rgem.gem.controllers.Validation;
import com.rr.rgem.gem.models.Answer;
import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.ConversationNode;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.models.Question;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private Set<String> answersLoaded = new HashSet<String>();
    private Map<String, String> varMap = new HashMap<String, String>();
    private Map<String, ConvoCallback> extFnMap = new HashMap<String, ConvoCallback>();
    Persisted persisted;
    String saved;
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
        persisted = new Persisted(context);
        saved = persisted.loadConvState(answerProcess.getName());
        if(Validation.isEmpty(saved)) {
            conversation = gson.fromJson(loadJsonFromResources(context),ConversationNode[].class);
        }else{
            conversation = gson.fromJson(saved,ConversationNode[].class);
        }
    }
    private void sendErrorAnswer(AppCompatActivity activity,TextView v){
        if (current.error != null && !current.error.equals(""))
        {
            //getState().setState(JSONState.State.Correct);
            ConversationNode error = getState().getNodeMap().get(getNextNode(current.error));
            Utils.toast(activity,error.text);
            //getState().sendChallenges(activity, "Invalid answer. Please enter a valid answer.");
            //v.setGravity(Gravity.END);
            //v.setEnabled(false);
        }else{
            Utils.toast(activity,"Invalid answer. Please enter a valid answer.");
            //getState().setState(JSONState.State.Incorrect);
            //getState().sendChallenges(activity, "Invalid answer. Please enter a valid answer.");

        }

    }
    private void sendSuccess(ApplicationActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();
        answer.setResponse(response);
        getState().setState(JSONState.State.Correct);
        current = getState().getNodeMap().get(getNextNode(answer.next));
        responseMap.put(answer.name, response);
        getState().sendChallenges(activity, "You answered: " + response);
        v.setGravity(Gravity.END);
        v.setEnabled(false);
    }
    private boolean textAnswer(ApplicationActivity activity,ConversationNode.AnswerNode answer, TextView v){

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
    private boolean mobileAnswer(ApplicationActivity activity,ConversationNode.AnswerNode answer,TextView v){
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
    private boolean currencyAnswer(ApplicationActivity activity,ConversationNode.AnswerNode answer,TextView v){

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
    private boolean dateAnswer(ApplicationActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();

        if(!Validation.isEmpty(response) && Validation.isValidDate(response) && answerProcess.dateAnswer(answer,response)){
            sendSuccess(activity,answer,v);
            return true;
        }else {
            sendErrorAnswer(activity, v);
        }
        return false;
    }
    private boolean numberAnswer(ApplicationActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();

        if(!Validation.isEmpty(response) && Validation.isValidNumber(response) && answerProcess.textAnswer(answer,response)){
            sendSuccess(activity,answer,v);
            return true;
        }else {
            sendErrorAnswer(activity, v);
        }
        return false;
    }
    private boolean passwordAnswer(ApplicationActivity activity,ConversationNode.AnswerNode answer,TextView v){
        String response = v.getText().toString();
        if(!Validation.isEmpty(response) && Validation.isValidDate(response) && answerProcess.passwordAnswer(answer,response)){
            sendSuccess(activity,answer,v);
            return true;
        }
        return false;

    }

    private boolean imageUploadAnswer(ApplicationActivity activity, ConversationNode.AnswerNode answer, String imageName) {
        File image = Utils.getFileFromName("imageDir", imageName, activity.getBaseContext());
        if (!Validation.isEmpty(imageName) && Utils.fileExists(image)) {
            answer.setResponse(imageName);
            getState().setState(JSONState.State.Correct);
            current = getState().getNodeMap().get(getNextNode(answer.next));
            responseMap.put(answer.name, imageName);
            getState().sendChallenges(activity, null);
            return true;
        }
        return false;
    }

    public void saveState(){
        String generated = toJson();
    }

    private boolean anyTextAnswer(final ApplicationActivity activity,ConversationNode.NodeType type,ConversationNode.AnswerNode answer,TextView v){
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
        return textAnswer(activity,answer,v);
    }

    public void displayQuestion(final ApplicationActivity activity, final LeftRightConversation conversationView, Question question, long questionId) {

        if(current.type == ConversationNode.NodeType.end) {
            Utils.toast(activity, current.text);
            state.setState(JSONState.State.Complete);
            getState().sendChallenges(activity, null);
            return;
        }

        if(current.type == ConversationNode.NodeType.info) {
            Utils.toast(activity, current.text);
            return;
        }

        final ConversationNode.AnswerNode answer = current.answers[0];
        //this.answerMap.put(questionId, answer);
        boolean answerLoaded = answersLoaded.contains(current.getName());
        if (current.type == ConversationNode.NodeType.choice) {
            Map<String, View.OnClickListener> listeners = new HashMap<String, View.OnClickListener>();
            for (final ConversationNode.AnswerNode choice: current.answers)
            {
                listeners.put(choice.value, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(JSONController.this.answerProcess.choiceAnswer(answer,choice.value)) {
                            JSONController.this.getState().setState(JSONState.State.Correct);
                            responseMap.put(choice.name, choice.value);
                            current = JSONController.this.getState().getNodeMap().get(getNextNode(choice.next));
                            JSONController.this.getState().sendChallenges(activity, "You selected: " + ((Button) v).getText());
                            choice.setResponse(choice.value);
                            answer.setResponse(choice.value);
                            JSONController.this.saveJson();
                            for (int j = 0; j < ((ViewGroup) v.getParent()).getChildCount(); ++j) {
                                ((ViewGroup) v.getParent()).getChildAt(j).setEnabled(false);
                            }
                        }else{

                        }
                    }
                });
            }


            if (!listeners.isEmpty()) {
                conversationView.addMultipleChoiceQuestion(questionId, question.getText(),answer.getResponse(), listeners);
                for (final ConversationNode.AnswerNode choice: current.answers) {
                    if (!Validation.isEmpty(choice.getResponse())&& !answerLoaded) {
                        answersLoaded.add(current.getName());
                        JSONController.this.getState().setState(JSONState.State.Correct);
                        responseMap.put(choice.name, choice.value);
                        current = JSONController.this.getState().getNodeMap().get(getNextNode(choice.next));
                        JSONController.this.getState().sendChallenges(activity, "You selected: " + choice.getResponse());
                        choice.setResponse(choice.value);
                        answer.setResponse(choice.value);
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
            if(!Validation.isEmpty(answer.getResponse()) && !answerLoaded){
                answersLoaded.add(current.getName());
                LinearLayout contentHolder = (LinearLayout) conversationView.getViews().get(questionId);
                TextView v = (TextView)contentHolder.findViewById(R.id.text);
                v.setText(answer.getResponse());
                v.setEnabled(false);
                passwordAnswer(activity,answer,v);
            }
        }else if(current.type == ConversationNode.NodeType.date) {

            conversationView.addDateInputQuestion(questionId, question.getText(),answer.getResponse(), new TextView.OnEditorActionListener() {
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
            if(!Validation.isEmpty(answer.getResponse()) && !answerLoaded){
                answersLoaded.add(current.getName());
                LinearLayout contentHolder = (LinearLayout) conversationView.getViews().get(questionId);
                TextView v = (TextView)contentHolder.findViewById(R.id.text);
                v.setText(answer.getResponse());
                v.setEnabled(false);
                anyTextAnswer(activity,current.type ,answer,v);
            }
        } else if (current.type == ConversationNode.NodeType.imageUpload) {
            Message message = new Message(1, "2016", true, Message.ResponseType.ImageUpload, null);
            message.setTitle(question.getText());
            activity.currentImage = conversationView.addImageUploadQuestion(message, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = activity.getSupportFragmentManager();
                    ImageUploadDialog dialog = new ImageUploadDialog();
                    dialog.show(manager, "dialog");
                }
            });

            if(!Validation.isEmpty(answer.getResponse()) && !answerLoaded){
                answersLoaded.add(current.getName());
                ImageView v = activity.currentImage;
                File imageFile = Utils.getFileFromName("imageDir", answer.getResponse(), activity.getBaseContext());
                if (Utils.fileExists(imageFile))
                {
                    try {
                        Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(imageFile));
                        v.setImageBitmap(photo);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
                imageUploadAnswer(activity, answer, answer.getResponse());
            }

        } else{
            conversationView.addTextInputQuestion(questionId, question.getText(),answer.getResponse(), new TextView.OnEditorActionListener() {
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
            if(!Validation.isEmpty(answer.getResponse()) && !answerLoaded){
                answersLoaded.add(current.getName());
                LinearLayout contentHolder = (LinearLayout) conversationView.getViews().get(questionId);
                TextView v = (TextView)contentHolder.findViewById(R.id.text);
                v.setText(answer.getResponse());
                v.setEnabled(false);
                anyTextAnswer(activity,current.type ,answer,v);
            }
        }
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

    public void ImageUploaded(ApplicationActivity activity, String imageName)
    {
        if(imageUploadAnswer(activity, current.answers[0], imageName)) {
            JSONController.this.saveJson();
        }
    }
}
