package com.nike.dooit.views.main;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.nike.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsche on 2016/11/05.
 */

public enum MainViewPagerPositions {
    BOT(0, null, R.drawable.bot),
    TARGET(1, R.string.main_tab_text_1, null),
    CHALLENGE(2, R.string.main_tab_text_2, null),
    TIPS(3, R.string.main_tab_text_3, null);

    private static Map<Integer, MainViewPagerPositions> map = new HashMap<Integer, MainViewPagerPositions>();

    static {
        for (MainViewPagerPositions cardType : MainViewPagerPositions.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;
    private final Integer titleRes;
    private final Integer iconRes;

    MainViewPagerPositions(int value, @StringRes Integer titleRes, @DrawableRes Integer iconRes) {
        this.value = value;
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    public static MainViewPagerPositions getValueOf(int value) {
        return map.containsKey(value) ? map.get(value) : BOT;
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
