package org.gem.indo.dooit.models.bot;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public abstract class BotCallback {

    protected Context context;
    protected OnAsyncListener listener;
    protected Handler handler;

    protected BotCallback(Context context) {
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
     * Called when the `asyncCall` field is set on a Node.
     *
     * @param key       The value of the `callback` field
     * @param answerLog The Answer Log up to the point of the calling Node
     * @param model     The calling Node or Answer
     * @param listener  Listener to be called when async operation is done
     */
    public void onAsyncCall(String key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {

    }

    /**
     * Provide a conversation level model object that a Node may require.
     *
     * @return
     */
    public Object getObject() {
        return null;
    }

    protected void notifyDone(final OnAsyncListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDone();
            }
        });
    }

    public interface OnAsyncListener {
        void onDone();
    }
}
