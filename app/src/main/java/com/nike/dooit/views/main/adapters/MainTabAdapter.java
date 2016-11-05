package com.nike.dooit.views.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nike.dooit.views.main.MainViewPagerPositions;
import com.nike.dooit.views.main.fragments.BotFragment;

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
                fragment = new BotFragment().newInstance();
                break;
            case TARGET:
                fragment = new BotFragment().newInstance();
                break;
            case CHALLENGE:
                fragment = new BotFragment().newInstance();
                break;
            case TIPS:
                fragment = new BotFragment().newInstance();
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
