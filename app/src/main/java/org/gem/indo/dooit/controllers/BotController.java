package org.gem.indo.dooit.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public abstract class BotController {

    protected Context context;
    private Handler handler;
    final protected BotType botType;

    BotController(Context context, BotType botType) {
        this.context = context;
        this.botType = botType;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Called when the conversation reaches an `end` Node.
     *
     * @param answerLog
     */
    public void onDone(Map<String, Answer> answerLog) {

    }

    /**
     * Called when the `callback` field is set on a Node.
     *
     * @param key       The value of the `callback` field
     * @param answerLog The Answer Log up to the point of the calling Node
     * @param model     The calling Node or Answer
     */
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    /**
     * Called by viewholders trigger behaviour in the controller.
     *
     * @param model
     * @param key
     */
    public void call(BaseBotModel model, String key) {
        // Override me
    }

    /**
     * Called when the `asyncCall` field is set on a Node.
     *
     * @param key       The value of the `callback` field
     * @param answerLog The Answer Log up to the point of the calling Node
     * @param model     The calling Node or Answer
     * @param listener  Listener to be called when async operation is done
     */
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        // Override me
    }

    protected void notifyDone(final OnAsyncListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDone();
            }
        });
    }

    /**
     * @param model     The bot model that needs the parameter value
     * @param paramType The key of the parameter
     */
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        // Override me
    }

    // TODO: Currently unused. Input is retrieved by scanning conversation history. We need to
    // decide how to store values while the "Accept" or "Cancel" is outstanding.
    public abstract void onAnswerInput(BotParamType inputType, Answer answer);

    /**
     * Called after any Answer has been inputted, and before the next Node has been added.
     *
     * @param answer Any inputted answer.
     */
    public void onAnswer(Answer answer) {
        // Override me
    }

    /**
     * Provide a conversation level model object that a Node may require.
     *
     * @return
     */
    public Object getObject() {
        return null;
    }

    /**
     * Provide a conversation level model object that a Node may require.
     *
     * @param objType The business model object type the view holder wants.
     * @return
     */
    public Object getObject(BotObjectType objType) {
        return null;
    }

    public BotType getBotType() {
        return botType;
    }

    /**
     * Allows the controller to filter out quick answers that should not be shown to the user.
     *
     * @param models The list of models used for creating the quick answers
     * @return A new list with unwanted answers filtered out
     */
    public final List<Answer> filterQuickAnswers(List<Answer> models) {
        List<Answer> answers = new ArrayList<>();
        for (Answer answer : models)
            if (filterQuickAnswer(answer))
                answers.add(answer);
        return answers;
    }

    public boolean filterQuickAnswer(Answer answer) {
        return true;
    }

    /**
     * Allows the controller to decide whether a Node should be added. When `true` is returned, the
     * `next` Node will be added instead.
     *
     * @param model
     * @return Instructs the bot runner whether the Node should be skipped.
     */
    public boolean shouldSkip(BaseBotModel model) {
        return false;
    }

    public interface OnAsyncListener {
        void onDone();
    }
}
