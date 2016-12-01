package org.gem.indo.dooit.models.bot;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public interface BotCallback {
    /**
     * Called when the conversation reaches an `end` Node.
     *
     * @param answerLog
     */
    void onDone(Map<String, Answer> answerLog);

    /**
     * Called by the `callback` field.
     * @param key The value of the `callback` field
     * @param answerLog The Answer Log up to the point of the calling Node
     * @param model The calling Node or Answer
     */
    void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model);
}
