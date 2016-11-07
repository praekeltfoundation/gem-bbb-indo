package com.nike.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.nike.dooit.R;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.views.main.tabs.TipTabContent;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TipsFragment extends Fragment {

    private static final String TAG = "TipArchive";
    private static final String TAB_FAVOURITES = "favourites";
    private static final String TAB_ALL = "all";

    @BindString(R.string.tips_tab_favourites)
    String favouritesTitle;

    @BindString(R.string.tips_tab_all)
    String allTitle;

    @BindView(R.id.fragment_tips_tabhost)
    TabHost tabHost;

    @Inject
    TipManager tipManager;

    public TipsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TipsFragment.
     */
    public static TipsFragment newInstance() {
        TipsFragment fragment = new TipsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);
        ButterKnife.bind(this, view);

        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec(TAB_FAVOURITES)
                .setContent(new TipTabContent(getContext()))
                .setIndicator(favouritesTitle));

        tabHost.addTab(tabHost.newTabSpec(TAB_ALL)
                .setContent(new TipTabContent(getContext()))
                .setIndicator(allTitle));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_tips, menu);
    }
}
