package com.nike.dooit.views.main.fragments.tip;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nike.dooit.R;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipsTabAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 2;

    private Context context;

    public TipsTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (TipsViewPagerPositions.getValueOf(position)) {
            case FAVOURITES:
                return TipsListFragment.newInstance(TipsViewPagerPositions.FAVOURITES.getValue());
            case ALL:
                return TipsListFragment.newInstance(TipsViewPagerPositions.ALL.getValue());
            default:
                return TipsListFragment.newInstance(TipsViewPagerPositions.ALL.getValue());
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_tips, null);

        TipsViewPagerPositions pos = TipsViewPagerPositions.getValueOf(position);
        TextView textView = (TextView) view.findViewById(R.id.tab_tip_textview);

        textView.setText(context.getString(pos.getTitleRes()));
        return view;
    }
}
