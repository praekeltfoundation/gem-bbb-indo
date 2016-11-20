package com.nike.dooit.models.bot;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public interface BotCallback {
    void onDone(Map<String, Answer> answerLog);
}
