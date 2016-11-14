package com.nike.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.main.MainViewPagerPositions;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetFragment extends MainFragment {
    public TargetFragment() {
        // Required empty public constructor
    }

    public static TargetFragment newInstance() {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_target, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_target, menu);
    }

    @OnClick(R.id.fragment_target_button)
    public void buttonClick(View view) {
        startBot(BotType.GOAL);
    }
}
