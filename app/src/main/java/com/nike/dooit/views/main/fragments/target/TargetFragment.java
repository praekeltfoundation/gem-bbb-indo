package com.nike.dooit.views.main.fragments.target;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.Goal;
import com.nike.dooit.models.enums.BotType;
import com.nike.dooit.views.custom.WeekGraph;
import com.nike.dooit.views.helpers.activity.CurrencyHelper;
import com.nike.dooit.views.main.fragments.MainFragment;
import com.nike.dooit.views.main.fragments.target.adapters.TargetPagerAdapter;

import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class TargetFragment extends MainFragment {

    @BindView(R.id.fragment_target_targets_view_pagers)
    ViewPager viewPager;

    @BindView(R.id.fragment_target_goal_name_text_view)
    TextView goalName;

    @BindView(R.id.fragment_target_saved_text_view)
    TextView saved;

    @BindView(R.id.fragment_target_total_text_view)
    TextView total;

    @BindView(R.id.fragment_target_savings_plan_message)
    TextView targetMessage;

    @BindView(R.id.fragment_target_week_graph_view)
    WeekGraph bars;

    @BindView(R.id.fragment_target_left_image_button)
    ImageButton leftTarget;

    @BindView(R.id.fragment_target_right_image_button)
    ImageButton rightTarget;

    @BindString(R.string.savings_message)
    String savingsMessage;

    @Inject
    Persisted persisted;

    @Inject
    GoalManager goalManager;

    private List<Goal> goals;
    private TargetPagerAdapter adapter;

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

                if (position == 0)
                    leftTarget.setVisibility(View.GONE);
                else
                    leftTarget.setVisibility(View.VISIBLE);

                if (position == goals.size())
                    rightTarget.setVisibility(View.GONE);
                else
                    rightTarget.setVisibility(View.VISIBLE);
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
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_main_target, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_target_add_goal:
                startBot(BotType.GOAL_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.fragment_target_left_image_button)
    public void onLeftClick() {
        viewPager.setCurrentItem(Math.max(0, viewPager.getCurrentItem() - 1));
    }

    @OnClick(R.id.fragment_target_right_image_button)
    public void onRightClick() {
        viewPager.setCurrentItem(Math.min(goals.size(), viewPager.getCurrentItem() + 1));
    }

    @OnClick(R.id.fragment_target_save_button)
    public void onSavingsEditClick(View view) {
        Goal goal = goals.get(viewPager.getCurrentItem());
        persisted.saveConvoGoal(BotType.GOAL_DEPOSIT, goal);
        startBot(BotType.GOAL_DEPOSIT);
    }

    private void populateGoal(Goal goal) {
        goalName.setText(goal.getName());
        saved.setText(CurrencyHelper.format(goal.getValue()));
        total.setText(String.format("of %s", CurrencyHelper.format(goal.getTarget())));
        int weeks = Weeks.weeksBetween(goal.getStartDate(), goal.getEndDate()).getWeeks();
        double toSavePerWeek = goal.getTarget() / weeks;
        bars.setValues(goal.getWeeklyTotals());
        targetMessage.setText(String.format(savingsMessage, Currency.getInstance(getLocal()).getCurrencyCode(), toSavePerWeek, goal.getTarget(), weeks));
    }

    private void retrieveGoals() {
        goalManager.retrieveGoals(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Error retrieving goals.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        })
                .subscribe(new Action1<List<Goal>>() {
                    @Override
                    public void call(final List<Goal> goals) {
                        TargetFragment.this.goals = goals;
                        if (getActivity() != null && goals != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.updateGoals(goals);
                                    if (goals.size() > 0) {
                                        populateGoal(goals.get(0));
                                        rightTarget.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                    }
                });

        bars.setGoal(16.0f);
    }
}
