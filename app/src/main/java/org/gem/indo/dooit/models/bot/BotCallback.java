package org.gem.indo.dooit.models.bot;

import android.content.Context;

import org.gem.indo.dooit.models.enums.BotCallbackObjectType;

import java.util.Map;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public abstract class BotCallback {

    protected Context context;

    protected BotCallback(Context context) {
        this.context = context;
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
     * Called when the `asyncCall` field is set on a Node.
     *
     * @param key       The value of the `callback` field
     * @param answerLog The Answer Log up to the point of the calling Node
     * @param model     The calling Node or Answer
     */
    public Observable<Object> onAsyncCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {
        return null;
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
     * Provide a conversation level model object that a Node may require, based on provided key.
     */
    public Object getObject(BotCallbackObjectType objType) {
        return null;
    }
}
