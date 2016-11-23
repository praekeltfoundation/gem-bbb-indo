package org.gem.indo.dooit.views.welcome;

import android.support.annotation.DrawableRes;

import org.gem.indo.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsche on 2016/11/05.
 */

public enum WelcomeViewPagerPositions {
    ONE(0, "asset:///onboarding_01.gif", R.color.yellow, R.string.welcome_page_1),
    TWO(1, "asset:///onboarding_02.gif", R.color.yellow, R.string.welcome_page_2),
    THREE(2, "asset:///onboarding_03.gif", R.color.yellow, R.string.welcome_page_3);

    private static Map<Integer, WelcomeViewPagerPositions> map = new HashMap<Integer, WelcomeViewPagerPositions>();

    static {
        for (WelcomeViewPagerPositions cardType : WelcomeViewPagerPositions.values()) {
            map.put(cardType.value, cardType);
        }
    }

    private final int value;
    private final String animUri;
    private final Integer imageRes;
    private final Integer textRes;

    WelcomeViewPagerPositions(int value, String animUri, /*@DrawableRes*/ Integer iconRes, Integer textRes) {
        this.value = value;
        this.animUri = animUri;
        this.imageRes = iconRes;
        this.textRes = textRes;
    }

    public static WelcomeViewPagerPositions getValueOf(int value) {
        return map.containsKey(value) ? map.get(value) : ONE;
    }

    public int getValue() {
        return value;
    }

    public String getAnimUri() {
        return animUri;
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
