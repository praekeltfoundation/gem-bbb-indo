package com.nike.dooit.views.main.fragments.tip;

import com.nike.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public enum TipsViewPagerPositions {
    FAVOURITES(0, R.string.tips_tab_favourites),
    ALL(1, R.string.tips_tab_all);

    private static Map<Integer, TipsViewPagerPositions> map = new HashMap<Integer, TipsViewPagerPositions>();

    static {
        for (TipsViewPagerPositions pos : TipsViewPagerPositions.values()) {
            map.put(pos.getValue(), pos);
        }
    }

    private final int value;
    private final int titleRes;

    TipsViewPagerPositions(int value, int titleRes) {
        this.value = value;
        this.titleRes = titleRes;
    }

    public static TipsViewPagerPositions getValueOf(int value) {
        return map.containsKey(value) ? map.get(value) : ALL;
    }

    public int getValue() {
        return value;
    }

    public int getTitleRes() {
        return titleRes;
    }
}
