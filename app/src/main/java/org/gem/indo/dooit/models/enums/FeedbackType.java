package org.gem.indo.dooit.models.enums;

import android.support.annotation.StringRes;
import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.R;

/**
 * Created by Rudolph Jacobs on 2016-12-05.
 */

public enum FeedbackType {
    @SerializedName("ask")
    ASK(0, R.string.feedback_option_ask),

    @SerializedName("report")
    REPORT(1, R.string.feedback_option_report),

    @SerializedName("general")
    GENERAL(2, R.string.feedback_option_general),

    @SerializedName("partner")
    PARTNER(3, R.string.feedback_option_partner);

    static final SparseArray<FeedbackType> map = new SparseArray<>();

    static {
        for (FeedbackType feedbackType : FeedbackType.values()) {
            map.put(feedbackType.value, feedbackType);
        }
    }

    private final int value;
    private final int textRes;

    FeedbackType(int value, @StringRes int textRes) {
        this.value = value;
        this.textRes = textRes;
    }

    public static FeedbackType getValueOf(int value) {
        FeedbackType val = map.get(value);
        return val == null ? null : map.get(value);
    }

    public int getText() {
        return this.textRes;
    }

    public int getValue() {
        return this.value;
    }
}
