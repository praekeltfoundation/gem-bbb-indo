package com.nike.dooit.models.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public enum ChallengeType {
    INVALID(0, null),
    @SerializedName("quiz") QUIZ(1, "quiz"),
    @SerializedName("picture") PICTURE(2, "picture"),
    @SerializedName("freeform") FREEFORM(3, "freeform");

    private static Map<Integer, ChallengeType> map = new HashMap<Integer, ChallengeType>();

    static {
        for (ChallengeType cardType : ChallengeType.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;
    private final String name;

    ChallengeType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ChallengeType getValueOf(int value) {
        return map.containsKey(value) ? map.get(value) : INVALID;
    }

    public int getValue() {
        return value;
    }
}
