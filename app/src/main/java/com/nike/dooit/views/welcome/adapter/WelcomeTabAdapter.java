package com.nike.dooit.views.welcome.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nike.dooit.views.welcome.WelcomeViewPagerPositions;
import com.nike.dooit.views.welcome.fragments.WelcomeFragment;

import static com.nike.dooit.views.welcome.WelcomeViewPagerPositions.ONE;
import static com.nike.dooit.views.welcome.WelcomeViewPagerPositions.THREE;
import static com.nike.dooit.views.welcome.WelcomeViewPagerPositions.TWO;

/**
 * Created by wsche on 2016/11/05.
 */

public class WelcomeTabAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public WelcomeTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (WelcomeViewPagerPositions.getValueOf(position)) {
            case ONE:
                fragment = WelcomeFragment.newInstance(ONE.getImageRes());
                break;
            case TWO:
                fragment = WelcomeFragment.newInstance(TWO.getImageRes());
                break;
            case THREE:
                fragment = WelcomeFragment.newInstance(THREE.getImageRes());
                break;
            default:
                fragment = WelcomeFragment.newInstance(ONE.getImageRes());
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return WelcomeViewPagerPositions.values().length;
    }
}
