package com.nike.dooit.views.main.fragments.challenge;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.nike.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rjacobs on 2016/11/08.
 */

public enum ChallengeViewPagerPositions {
    REGISTER(0, null, R.drawable.ic_d_bot),
    QUIZ(1, R.string.main_tab_text_1, null),
    PICTURE(2, R.string.main_tab_text_2, null),
    FREEFORM(3, R.string.main_tab_text_3, null);

    private static Map<Integer, ChallengeViewPagerPositions> map = new HashMap<Integer, ChallengeViewPagerPositions>();

    static {
        for (ChallengeViewPagerPositions cardType : ChallengeViewPagerPositions.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;
    private final Integer titleRes;
    private final Integer iconRes;

    ChallengeViewPagerPositions(int value, @StringRes Integer titleRes, @DrawableRes Integer iconRes) {
        this.value = value;
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    public static ChallengeViewPagerPositions getValueOf(int value) {
        return map.containsKey(value) ? map.get(value) : REGISTER;
    }

    public int getValue() {
        return value;
    }

    @StringRes
    public Integer getTitleRes() {
        return titleRes;
    }

    @DrawableRes
    public Integer getIconRes() {
        return iconRes;
    }

}
