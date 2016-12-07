package org.gem.indo.dooit.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public abstract class BotController {

    protected Context context;
    protected OnAsyncListener listener;
    protected Handler handler;

    protected BotController(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Called when the conversation reaches an `end` Node.
     *
     * @param answerLog
     */
    public abstract void onDone(Map<String, Answer> answerLog);

    /**
     * Called when the `callback` field is set on a Node.
     *
     * @param key       The value of the `callback` field
     * @param answerLog The Answer Log up to the point of the calling Node
     * @param model     The calling Node or Answer
     */
    public abstract void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model);

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
    public void onAsyncCall(String key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
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

    public abstract void input(BotParamType inputType, Object value);

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

    public interface OnAsyncListener {
        void onDone();
    }
}
