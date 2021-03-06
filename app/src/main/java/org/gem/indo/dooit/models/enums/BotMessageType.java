package org.gem.indo.dooit.models.enums;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public enum BotMessageType {
    UNDEFINED(0),
    TEXT(1),
    GOALSELECTION(2),
    ANSWER(3),
    INLINETEXT(4),
    GALLERYUPLOAD(5),
    CAMERAUPLOAD(6),
    IMAGE(7),
    BLANK(8), // Not added to conversation history at all
    INLINENUMBER(9),
    INLINECURRENCY(10),
    INLINEDATE(11),
    TEXTCURRENCY(12),
    GOALGALLERY(13),
    GOALVERIFICATION(14),
    STARTCONVO(15),
    TIP(16),
    BLANKANSWER(17), // Answer that displays nothing
    END(18),
    GOALINFO(19),
    BADGE(20),
    GOALLISTSUMMARY(21),
    CHALLENGE(22),
    CHALLENGEPARTICIPANT(23),
    DUMMY(24), // Added to conversation history, but doesn't display
    GOALINFORMATIONGALLERY(25),
    EXPENSECATEGORYGALLERY(26),
    BUDGETINFO(27),
    GOALWEEKLYTARGETLISTSUMMARY(28);

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

    public static BotMessageType getValueOf(String name) {
        if (TextUtils.isEmpty(name))
            return UNDEFINED;
        return valueOf(name.toUpperCase());
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

    /**
     * @param msgType
     * @return Does the node contain text that may require parameters?
     */
    public boolean isTextType(BotMessageType msgType) {
        switch (msgType) {
            case TEXT:
                return true;
            default:
                return false;
        }
    }
}
