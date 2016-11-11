package com.nike.dooit.models.bot;

import com.nike.dooit.models.enums.BotMessageType;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class InputAnswer extends Answer {

    public InputAnswer() {
        type = BotMessageType.ANSWER.toString();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setName(String name) {
        this.name = name;
    }
}
