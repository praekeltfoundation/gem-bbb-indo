package org.gem.indo.dooit.views.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.bot.BotFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragment;
import org.gem.indo.dooit.views.main.fragments.target.TargetFragment;
import org.gem.indo.dooit.views.main.fragments.tip.TipsFragment;

/**
 * Created by wsche on 2016/11/05.
 */

public class MainTabAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private BotFragment botFragment = BotFragment.newInstance();
    private TargetFragment targetFragment = TargetFragment.newInstance();
    private ChallengeFragment challengeFragment = ChallengeFragment.newInstance();
    private TipsFragment tipsFragment = TipsFragment.newInstance();
    private boolean instantiated = false;
    private MainTabAdapterListener listener;

    public MainTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.instantiated = false;
    }

    @Override
    public Fragment getItem(int position) {
        switch (MainViewPagerPositions.getValueOf(position)) {
            case BOT:
                return botFragment;
            case TARGET:
                return targetFragment;
            case CHALLENGE:
                return challengeFragment;
            case TIPS:
                return tipsFragment;
            default:
                return botFragment;
        }
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
        View tabView = LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.tab_custom, null);
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

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
        if (!instantiated) {
            instantiated = true;
            if (listener != null)
                listener.onAdapterInstantiated();
        }

    }

    public void setAdapterListener(MainTabAdapterListener listener) {
        this.listener = listener;
    }

    public interface MainTabAdapterListener {
        void onAdapterInstantiated();
    }
}
