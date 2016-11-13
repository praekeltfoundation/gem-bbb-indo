package com.nike.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.nike.dooit.R;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.models.Tip;
import com.nike.dooit.views.main.fragments.tip.OnTipsAvailableListener;
import com.nike.dooit.views.main.fragments.tip.adapters.TipsAutoCompleteAdapter;
import com.nike.dooit.views.main.fragments.tip.adapters.TipsTabAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TipsFragment extends Fragment implements OnTipsAvailableListener {

    private static final String TAG = "TipArchive";

    @BindString(R.string.tips_tab_favourites)
    String favouritesTitle;

    @BindString(R.string.tips_tab_all)
    String allTitle;

    @BindView(R.id.fragment_tips_search_view)
    AutoCompleteTextView searchView;

    @BindView(R.id.fragment_tips_tablayout)
    TabLayout tabLayout;

    @BindView(R.id.fragment_tips_viewpager)
    ViewPager viewPager;

    @Inject
    TipManager tipManager;

    TipsAutoCompleteAdapter searchAdapter;

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

        searchAdapter = new TipsAutoCompleteAdapter(getContext(), R.layout.list_tips_item);
        searchView.setAdapter(searchAdapter);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tip tip = searchAdapter.getItem(position);
                Toast.makeText(getContext(),
                        String.format("Opening '%s'", tip.getTitle()), Toast.LENGTH_SHORT).show();
            }
        });

        TipsTabAdapter adapter = new TipsTabAdapter(getChildFragmentManager(), getContext(), this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }

        return view;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_tips, menu);
    }

    @Override
    public void onTipsAvailable(List<Tip> tips) {
        Log.d(TAG, "Updating Tips");
        searchAdapter.updateAllTips(tips);
    }
}
