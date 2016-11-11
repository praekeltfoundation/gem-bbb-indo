package com.nike.dooit.models.bot;

import android.util.Pair;

import com.nike.dooit.models.enums.BotMessageType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Answer extends BaseBotModel {
    private String value;
    private String next;
    private String removeOnSelect;
    private String[] changeOnSelect;

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

    public Pair<String, String> getChangeOnSelect() {
        if (changeOnSelect != null && changeOnSelect.length == 2)
            return new Pair<String, String>(changeOnSelect[0], changeOnSelect[1]);
        return null;
    }
}
