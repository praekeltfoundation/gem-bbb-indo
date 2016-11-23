package org.gem.indo.dooit.views.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.bot.BotFragment;
import org.gem.indo.dooit.views.main.fragments.ChallengeFragment;
import org.gem.indo.dooit.views.main.fragments.target.TargetFragment;
import org.gem.indo.dooit.views.main.fragments.TipsFragment;

/**
 * Created by wsche on 2016/11/05.
 */

public class MainTabAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public MainTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (MainViewPagerPositions.getValueOf(position)) {
            case BOT:
                fragment = BotFragment.newInstance();
                break;
            case TARGET:
                fragment = TargetFragment.newInstance();
                break;
            case CHALLENGE:
                fragment = ChallengeFragment.newInstance();
                break;
            case TIPS:
                fragment = TipsFragment.newInstance();
                break;
            default:
                fragment = BotFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return MainViewPagerPositions.values().length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (MainViewPagerPositions.getValueOf(position).getTitleRes() == null)
            return "";
        return context.getString(MainViewPagerPositions.getValueOf(position).getTitleRes());
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View tabView = LayoutInflater.from(context).inflate(R.layout.tab_custom, null);
        tabView.setTag(MainViewPagerPositions.getValueOf(position).getTitleRes());
        ImageView img = (ImageView) tabView.findViewById(R.id.tab_custom_icon);
        TextView title = (TextView) tabView.findViewById(R.id.tab_custom_title);
        Integer imgR = MainViewPagerPositions.getValueOf(position).getIconRes();
        if (imgR != null)
            img.setImageResource(imgR);
        else
            img.setVisibility(View.GONE);
        Integer stringR = MainViewPagerPositions.getValueOf(position).getTitleRes();
        if (stringR != null)
            title.setText(getPageTitle(position));
        else
            title.setVisibility(View.GONE);
        return tabView;
    }
}
