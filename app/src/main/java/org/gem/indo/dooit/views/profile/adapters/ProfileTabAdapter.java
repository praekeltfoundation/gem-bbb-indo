package org.gem.indo.dooit.views.profile.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.gem.indo.dooit.views.profile.enums.ProfileTabPosition;
import org.gem.indo.dooit.views.profile.fragments.AchievementFragment;
import org.gem.indo.dooit.views.profile.fragments.BudgetFragment;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public class ProfileTabAdapter extends FragmentStatePagerAdapter {

    private Context context;

    public ProfileTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        ProfileTabPosition pos = ProfileTabPosition.byId(position);
        if (pos == null)
            return AchievementFragment.newInstance();
        switch (pos) {
            case BUDGET:
                return BudgetFragment.newInstance();
            case ACHIEVEMENTS:
            default:
                return AchievementFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return ProfileTabPosition.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ProfileTabPosition pos = ProfileTabPosition.byId(position);
        if (pos != null && context != null)
            return context.getString(pos.getTextRes());
        return "";
    }
}
