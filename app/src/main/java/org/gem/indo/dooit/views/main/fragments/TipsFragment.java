package org.gem.indo.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.OnTipsAvailableListener;
import org.gem.indo.dooit.views.main.fragments.tip.TipsViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsAutoCompleteAdapter;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsTabAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnPageChange;
import butterknife.OnTouch;

public class TipsFragment extends Fragment implements OnTipsAvailableListener {

    private static final String TAG = TipsFragment.class.getName();

    @BindString(org.gem.indo.dooit.R.string.tips_list_filter)
    String filterText;

    @BindString(org.gem.indo.dooit.R.string.tips_tab_favourites)
    String favouritesTitle;

    @BindString(org.gem.indo.dooit.R.string.tips_tab_all)
    String allTitle;

    @BindString(org.gem.indo.dooit.R.string.tips_article_opening)
    String openingArticleText;

    @BindView(R.id.fragment_tips_search_view)
    AutoCompleteTextView searchView;

    @BindView(R.id.fragment_tips_tablayout)
    TabLayout tabLayout;

    @BindView(R.id.fragment_tips_viewpager)
    ViewPager viewPager;

    @BindView(R.id.fragment_tips_list_filter)
    ViewGroup filterView;

    @BindView(R.id.fragment_tips_list_filter_text_view)
    TextView filterTextView;

    @Inject
    TipManager tipManager;

    TipsTabAdapter tipsTabAdapter;
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
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_tips, container, false);
        ButterKnife.bind(this, view);

        searchAdapter = new TipsAutoCompleteAdapter(getContext(), org.gem.indo.dooit.R.layout.item_tips_search_suggestion);
        searchView.setAdapter(searchAdapter);

        tipsTabAdapter = new TipsTabAdapter(getChildFragmentManager(), getContext(), this);
        viewPager.setAdapter(tipsTabAdapter);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, viewPager);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(tipsTabAdapter.getTabView(i));
        }

        // Set initial search hint
        String hint = getString(TipsViewPagerPositions.getValueOf(
                viewPager.getCurrentItem()).getSearchRes());
        searchView.setHint(hint);

        return view;
    }
    @OnClick(R.id.fragment_tips_list_filter_image_button)
    public void clearFilter(View v) {
        tipsTabAdapter.getPrimaryItem().clearFilter(v);
    }

    @OnEditorAction(R.id.fragment_tips_search_view)
    public boolean onSearchSubmit(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String constraint = v.getText().toString();
            if (!TextUtils.isEmpty(constraint)) {
                Log.d(TAG, "On Search " + constraint);
                showFiltering(constraint);

                tipsTabAdapter.getPrimaryItem().onSearch(constraint);
            }
            return true;
        }
        return false;
    }

    @OnTouch(R.id.fragment_tips_search_view)
    public boolean onSearchClick(TextView v, MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - searchView.getPaddingRight())) {
                String constraint = v.getText().toString();
                if (!TextUtils.isEmpty(constraint))
                    tipsTabAdapter.getPrimaryItem().onSearch(constraint);
                return true;
            }
        }
        return false;
    }

    @OnPageChange(R.id.fragment_tips_viewpager)
    public void onPageSelected(int position) {
        TipsViewPagerPositions pos = TipsViewPagerPositions.getValueOf(position);
        searchView.setHint(getString(pos.getSearchRes()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main_tips, menu);
    }

    @Override
    public void onTipsAvailable(List<Tip> tips) {
        Log.d(TAG, "Updating Tips");
        searchAdapter.updateAllTips(tips);
    }

    protected void showFiltering(String constraint) {
        filterTextView.setText(String.format(filterText, constraint));
        filterView.setVisibility(View.VISIBLE);
    }

    public void hideFiltering() {
        filterView.setVisibility(View.GONE);
    }
}
