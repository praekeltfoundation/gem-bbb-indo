package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.views.main.fragments.challenge.ChallengeViewPagerPositions;
import com.nike.dooit.views.main.fragments.ChallengeFragment;
import com.nike.dooit.views.main.fragments.TargetFragment;
import com.nike.dooit.views.main.fragments.TipsFragment;
import com.nike.dooit.views.main.fragments.bot.BotFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

/**
 * Created by wsche on 2016/11/05.
 */

public class ChallengeTabAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public ChallengeTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (ChallengeViewPagerPositions.getValueOf(position)) {
            case REGISTER:
                fragment = ChallengeRegisterFragment.newInstance();
                break;
//            case FREEFORM:
//                fragment = TargetFragment.newInstance();
//                break;
//            case PICTURE:
//                fragment = ChallengeFragment.newInstance();
//                break;
            case QUIZ:
                fragment = ChallengeQuizQuestionFragment.newInstance();
                break;
            default:
                fragment = ChallengeRegisterFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return ChallengeViewPagerPositions.values().length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (ChallengeViewPagerPositions.getValueOf(position).getTitleRes() == null)
            return "";
        return context.getString(ChallengeViewPagerPositions.getValueOf(position).getTitleRes());
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View tabView = LayoutInflater.from(context).inflate(R.layout.tab_custom, null);
        tabView.setTag(ChallengeViewPagerPositions.getValueOf(position).getTitleRes());
        ImageView img = (ImageView) tabView.findViewById(R.id.tab_custom_icon);
        TextView title = (TextView) tabView.findViewById(R.id.tab_custom_title);
        Integer imgR = ChallengeViewPagerPositions.getValueOf(position).getIconRes();
        if (imgR != null)
            img.setImageResource(imgR);
        else
            img.setVisibility(View.GONE);
        Integer stringR = ChallengeViewPagerPositions.getValueOf(position).getTitleRes();
        if (stringR != null)
            title.setText(getPageTitle(position));
        else
            title.setVisibility(View.GONE);
        return tabView;
    }
}
