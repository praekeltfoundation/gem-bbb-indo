package org.gem.indo.dooit.views.main.fragments.bot.listeners;

import org.gem.indo.dooit.models.bot.Answer;

/**
 * Created by Wimpie Victor on 2017/02/01.
 */

public interface OnBotInputListener {

    /**
     * Fired when the user clicks on a quick answer, or inputs text into an inline answer.
     *
     * @param answer The answer containing the input value.
     */
    void onAnswer(Answer answer);
}
