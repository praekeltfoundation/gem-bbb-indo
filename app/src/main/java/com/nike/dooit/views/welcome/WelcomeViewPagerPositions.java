package com.nike.dooit.views.welcome;

import android.support.annotation.DrawableRes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsche on 2016/11/05.
 */

public enum WelcomeViewPagerPositions {
    ONE(0, android.R.drawable.sym_def_app_icon),
    TWO(1, android.R.drawable.sym_def_app_icon),
    THREE(2, android.R.drawable.sym_def_app_icon);

    private static Map<Integer, WelcomeViewPagerPositions> map = new HashMap<Integer, WelcomeViewPagerPositions>();

    static {
        for (WelcomeViewPagerPositions cardType : WelcomeViewPagerPositions.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;
    private final Integer imageRes;

    WelcomeViewPagerPositions(int value, @DrawableRes Integer iconRes) {
        this.value = value;
        this.imageRes = iconRes;
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
}
