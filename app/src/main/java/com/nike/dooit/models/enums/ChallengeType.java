package com.nike.dooit.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rudolph Jacobs on 2016-11-11.
 */

public enum ChallengeType {
    @SerializedName("quiz") QUIZ,
    @SerializedName("picture") PICTURE,
    @SerializedName("freeform") FREEFORM
}
