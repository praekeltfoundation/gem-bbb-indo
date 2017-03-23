package org.gem.indo.dooit.helpers.bot;

import android.support.annotation.NonNull;

import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/28.
 */

public interface BotRunner {
    void queueNode(Node node);

    void addNode(Node node);

    /**
     * Returns the conversation history as a mapping from Answer names to actual Answers.
     * <p>
     * Also includes a mapping from associated Node names to their related Answers.
     *
     * @return
     */
    @NonNull
    Map<String, Answer> getAnswerLog();
}
