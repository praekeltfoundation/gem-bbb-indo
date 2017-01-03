package org.gem.indo.dooit.views.main;

import android.graphics.PorterDuff;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gem.indo.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Werner Scheffer on 2016/11/05.
 */

public enum MainViewPagerPositions {
    BOT(0, R.string.main_tab_text_0, R.drawable.ic_d_nav_bot),
    TARGET(1, R.string.main_tab_text_1, R.drawable.ic_d_nav_flag),
    CHALLENGE(2, R.string.main_tab_text_2, R.drawable.ic_d_nav_trophy),
    TIPS(3, R.string.main_tab_text_3, R.drawable.ic_d_nav_tips);

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

    public static void setActiveState(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.tab_custom_icon);
        icon.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.purple), PorterDuff.Mode.MULTIPLY);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) icon.getLayoutParams();
        lp.width = view.getContext().getResources().getDimensionPixelSize(R.dimen.nav_icon_size_small);
        lp.height = view.getContext().getResources().getDimensionPixelSize(R.dimen.nav_icon_size_small);

        TextView text = (TextView) view.findViewById(R.id.tab_custom_title);
        text.setVisibility(View.VISIBLE);
        text.setTextColor(ContextCompat.getColor(view.getContext(),R.color.purple));
    }

    public static void setInActiveState(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.tab_custom_icon);
        icon.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.light_grey), PorterDuff.Mode.MULTIPLY);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) icon.getLayoutParams();
        lp.width = view.getContext().getResources().getDimensionPixelSize(R.dimen.nav_icon_size_small);
        lp.height = view.getContext().getResources().getDimensionPixelSize(R.dimen.nav_icon_size_small);

        TextView text = (TextView) view.findViewById(R.id.tab_custom_title);
        text.setVisibility(View.VISIBLE);
        text.setTextColor(ContextCompat.getColor(view.getContext(),R.color.grey));
    }
}
