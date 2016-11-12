package com.nike.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;

import com.nike.dooit.R;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.main.MainViewPagerPositions;
import com.nike.dooit.views.main.fragments.bot.BotFragment;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class MainFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        onActive();
    }

    public void onActive() {

    }

    protected void startBot(BotType botType) {
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.content_main_view_pager);
        BotFragment botFragment = (BotFragment) viewPager.getAdapter().instantiateItem(viewPager, MainViewPagerPositions.BOT.getValue());
        botFragment.setBotType(botType);
        viewPager.setCurrentItem(MainViewPagerPositions.BOT.getValue());
    }
}
