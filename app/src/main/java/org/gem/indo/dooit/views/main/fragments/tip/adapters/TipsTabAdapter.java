package org.gem.indo.dooit.views.main.fragments.tip.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.main.fragments.tip.OnTipsAvailableListener;
import org.gem.indo.dooit.views.main.fragments.tip.TipsListFragment;
import org.gem.indo.dooit.views.main.fragments.tip.TipsViewPagerPositions;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipsTabAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 2;

    private Context context;
    private OnTipsAvailableListener listener;
    private TipsListFragment current;

    public TipsTabAdapter(FragmentManager fm, Context context, OnTipsAvailableListener listener) {
        super(fm);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        TipsListFragment fragment;
        switch (TipsViewPagerPositions.getValueOf(position)) {
            case FAVOURITES:
                fragment = TipsListFragment.newInstance(TipsViewPagerPositions.FAVOURITES.getValue());
                break;
            case ALL:
                fragment = TipsListFragment.newInstance(TipsViewPagerPositions.ALL.getValue());
                break;
            default:
                fragment = TipsListFragment.newInstance(TipsViewPagerPositions.ALL.getValue());
        }
        fragment.setOnTipsLoadedListener(listener);
        return fragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (current != object) {
            if (current != null) {
                current.setOnTipsLoadedListener(null);
                current.onPageDeselected();
            }
            current = (TipsListFragment) object;
            if (current != null) {
                current.setOnTipsLoadedListener(listener);
                current.onPageSelected();
            }
        }
        super.setPrimaryItem(container, position, object);
    }

    public TipsListFragment getPrimaryItem() {
        return current;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.tab_tips, null);

        TipsViewPagerPositions pos = TipsViewPagerPositions.getValueOf(position);
        TextView textView = (TextView) view.findViewById(R.id.tab_tip_textview);

        textView.setText(context.getString(pos.getTitleRes()));
        return view;
    }
}
