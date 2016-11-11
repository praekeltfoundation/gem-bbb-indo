package com.nike.dooit.models.enums;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public enum BotMessageType {
    UNDEFINED(0),
    TEXT(1),
    MULTILINETEXT(2),
    GOALSELECTION(3),
    ANSWER(4),
    INLINEEDIT(5);

    private static Map<Integer, BotMessageType> map = new HashMap<>();

    static {
        for (BotMessageType cardType : BotMessageType.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;

    BotMessageType(int value) {
        this.value = value;
    }

    public static BotMessageType getValueOf(int value) {
        if (map != null && map.containsKey(value))
            return map.get(value);
        return UNDEFINED;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public boolean equalsTo(int value) {
        return this.value == value;
    }

    public static BotMessageType getValueOf(String name) {
        if (TextUtils.isEmpty(name))
            return UNDEFINED;
        return valueOf(name.toUpperCase());
    }
}
