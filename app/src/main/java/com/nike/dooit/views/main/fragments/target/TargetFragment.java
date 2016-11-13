package com.nike.dooit.views.main.fragments.target;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.custom.WeekGraph;
import com.nike.dooit.views.main.fragments.MainFragment;
import com.nike.dooit.views.main.fragments.target.adapters.TargetPagerAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class TargetFragment extends MainFragment {

    @Inject
    GoalManager goalManager;

    @BindView(R.id.fragment_target_targets_view_pagers)
    ViewPager viewPager;
    @BindView(R.id.fragment_target_goal_name_text_view)
    TextView goalName;
    @BindView(R.id.fragment_target_saved_text_view)
    TextView saved;
    @BindView(R.id.fragment_target_total_text_view)
    TextView total;

    @BindView(R.id.fragment_target_week_graph_view)
    WeekGraph bars;

    private TargetPagerAdapter adapter;

    List<Goal> goals;

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

        adapter = new TargetPagerAdapter(new ArrayList<Goal>());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                populateGoal(goals.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        retrieveGoals();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_target, menu);
    }

    @OnClick(R.id.fragment_target_save_button)
    public void buttonClick() {
        startBot(BotType.GOAL);
    }

    @OnClick(R.id.fragment_target_left_image_button)
    public void onLeftClick() {
        viewPager.setCurrentItem(Math.max(0, viewPager.getCurrentItem() - 1));
    }

    @OnClick(R.id.fragment_target_right_image_button)
    public void onRightClick() {
        viewPager.setCurrentItem(Math.min(goals.size(), viewPager.getCurrentItem() + 1));
    }

    private void populateGoal(Goal goal) {
        goalName.setText(goal.getName());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        saved.setText(format.format(goal.getValue()));
        total.setText(String.format("of %s", format.format(goal.getTarget())));
    }

    private void retrieveGoals() {
        goalManager.retrieveGoals(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Error retrieving goals.", Toast.LENGTH_SHORT);
            }
        })
                .subscribe(new Action1<List<Goal>>() {
                    @Override
                    public void call(final List<Goal> goals) {
                        TargetFragment.this.goals = goals;
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.updateGoals(goals);
                                    populateGoal(goals.get(0));
                                }
                            });
                    }
                });
        bars.setValues(new LinkedHashMap() {{
            put("1", 1908.0f);
            put("2", 5329.0f);
            put("3", 7696.0f);
            put("4", 4389.0f);
            put("5", 4089.0f);
            put("6", 7648.0f);
            put("7", 3788.0f);
            put("8", 6025.0f);
            put("9", 6488.0f);
            put("10", 8907.0f);
            put("11", 6262.0f);
            put("12", 7305.0f);
            put("13", 2209.0f);
            put("14", 2498.0f);
            put("15", 8069.0f);
            put("16", 5342.0f);
        }});
    }
}
