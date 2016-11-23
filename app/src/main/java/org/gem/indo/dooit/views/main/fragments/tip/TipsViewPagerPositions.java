package org.gem.indo.dooit.views.main.fragments.tip;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.main.fragments.tip.providers.AllTips;
import org.gem.indo.dooit.views.main.fragments.tip.providers.FavouriteTips;
import org.gem.indo.dooit.views.main.fragments.tip.providers.TipProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public enum TipsViewPagerPositions {
    FAVOURITES(0, R.string.tips_tab_favourites, R.string.tips_search_placeholder_fav),
    ALL(1, R.string.tips_tab_all, R.string.tips_search_placeholder_all);

    private static Map<Integer, TipsViewPagerPositions> map = new HashMap<Integer, TipsViewPagerPositions>();

    static {
        for (TipsViewPagerPositions pos : TipsViewPagerPositions.values()) {
            map.put(pos.getValue(), pos);
        }
    }

    private final int value;
    private final int titleRes;
    private final int searchRes;

    TipsViewPagerPositions(int value, int titleRes, int searchRes) {
        this.value = value;
        this.titleRes = titleRes;
        this.searchRes = searchRes;
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

    public int getSearchRes() {
        return searchRes;
    }

    public TipProvider newProvider(DooitApplication application) {
        switch (this) {
            case ALL:
                return new AllTips(application);
            case FAVOURITES:
                return new FavouriteTips(application);
            default:
                return null;
        }
    }
}
