package com.nike.dooit.models.bot;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class Answer extends BaseBotModel {
    private String value;
    private String next;

    public String getNext() {
        return next;
    }

    public String getValue() {
        return value;
    }
}
