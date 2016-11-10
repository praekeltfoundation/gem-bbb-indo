package com.nike.dooit.models.bot;

import com.nike.dooit.models.enums.BotMessageType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Answer extends BaseBotModel {
    private String value;
    private String next;
    private String removeOnSelect;

    public Answer() {
        type = BotMessageType.ANSWER.name();
    }

    public String getNext() {
        return next;
    }

    public String getValue() {
        return value;
    }

    public String getRemoveOnSelect() {
        return removeOnSelect;
    }
}
