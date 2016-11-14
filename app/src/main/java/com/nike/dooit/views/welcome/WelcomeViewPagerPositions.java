package com.nike.dooit.views.welcome;

import android.support.annotation.DrawableRes;

import com.nike.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsche on 2016/11/05.
 */

public enum WelcomeViewPagerPositions {
    ONE(0, R.color.purple, R.string.welcome_page_1),
    TWO(1, R.color.purple, R.string.welcome_page_2),
    THREE(2, R.color.purple, R.string.welcome_page_3);

    private static Map<Integer, WelcomeViewPagerPositions> map = new HashMap<Integer, WelcomeViewPagerPositions>();

    static {
        for (WelcomeViewPagerPositions cardType : WelcomeViewPagerPositions.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;
    private final Integer imageRes;
    private final Integer textRes;

    WelcomeViewPagerPositions(int value, /*@DrawableRes*/ Integer iconRes, Integer textRes) {
        this.value = value;
        this.imageRes = iconRes;
        this.textRes = textRes;
    }

    public static WelcomeViewPagerPositions getValueOf(int value) {
        return map.containsKey(value) ? map.get(value) : ONE;
    }

    public int getValue() {
        return value;
    }

    public
    @DrawableRes
    int getImageRes() {
        return imageRes;
    }

    public Integer getTextRes() {
        return textRes;
    }
}
