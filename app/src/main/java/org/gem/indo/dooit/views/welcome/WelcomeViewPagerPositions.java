package org.gem.indo.dooit.views.welcome;

import android.support.annotation.DrawableRes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsche on 2016/11/05.
 */

public enum WelcomeViewPagerPositions {
    ONE(0, "asset:///onboarding_01.gif", org.gem.indo.dooit.R.color.yellow, org.gem.indo.dooit.R.string.welcome_page_1, "asset:///onboarding_static_01.png"),
    TWO(1, "asset:///onboarding_02.gif", org.gem.indo.dooit.R.color.yellow, org.gem.indo.dooit.R.string.welcome_page_2, "asset:///onboarding_static_02.png"),
    THREE(2, "asset:///onboarding_03.gif", org.gem.indo.dooit.R.color.yellow, org.gem.indo.dooit.R.string.welcome_page_3, "asset:///onboarding_static_03.png");

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
    private final String staticUri;

    WelcomeViewPagerPositions(int value, String animUri, /*@DrawableRes*/ Integer iconRes, Integer textRes, String staticUri) {
        this.value = value;
        this.animUri = animUri;
        this.imageRes = iconRes;
        this.textRes = textRes;
        this.staticUri = staticUri;

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

    public String getStaticUri() {
        return staticUri;
    }
}
