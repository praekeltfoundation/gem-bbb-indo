package org.gem.indo.dooit.views.main.fragments.target;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.custom.WeekGraph;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.target.adapters.TargetPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import rx.functions.Action1;

public class TargetFragment extends MainFragment {

    @BindView(R.id.fragment_target_nested_scroll_view)
    NestedScrollView nestedScrollView;

    @BindView(R.id.fragment_target_no_goals)
    View noGoalView;

    @BindView(R.id.fragment_target_add_goal_button)
    Button addGoal;

    @BindView(R.id.fragment_target_targets_view_pagers)
    ViewPager viewPager;

    @BindView(R.id.fragment_target_goal_name_text_view)
    TextView goalName;

    @BindView(R.id.fragment_target_saved_text_view)
    TextView saved;

    @BindView(R.id.fragment_target_total_text_view)
    TextView total;

    @BindView(R.id.fragment_target_savings_plan_message)
    TextView goalMessage;

    @BindView(R.id.fragment_target_left_image_button)
    ImageButton leftTarget;

    @BindView(R.id.fragment_target_right_image_button)
    ImageButton rightTarget;

    @BindView(R.id.fragment_target_loading_progress_container)
    View loadingProgress;

    @BindView(R.id.fragment_target_deposit_button)
    Button depositButton;

    @BindView(R.id.fragment_target_withdraw_button)
    Button withdrawButton;

    @BindString(R.string.target_savings_message)
    String savingsMessage;

    @BindString(R.string.target_of)
    String of_goal_total;

    @BindString(R.string.error_retrieve_goals)
    String error_retrieving_goals;

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);

        adapter = new TargetPagerAdapter(new ArrayList<Goal>());
        viewPager.setAdapter(adapter);

        if (adapter.getCount() == 0) {
            rightTarget.setVisibility(View.GONE);
            leftTarget.setVisibility(View.GONE);
        }

        retrieveGoals();
    }

    @OnPageChange(value = R.id.fragment_target_targets_view_pagers, callback = OnPageChange.Callback.PAGE_SELECTED)
    void onPageSelected(int position) {
        populateGoal(goals.get(position));
        updateNavCarets();
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

    @OnClick(R.id.fragment_target_deposit_button)
    public void onDepositClick(View view) {
        Goal goal = goals.get(viewPager.getCurrentItem());
        persisted.saveConvoGoal(BotType.GOAL_DEPOSIT, goal);
        startBot(BotType.GOAL_DEPOSIT);
    }

    @OnClick(R.id.fragment_target_withdraw_button)
    public void onWithdrawClick(View view) {
        Goal goal = goals.get(viewPager.getCurrentItem());
        persisted.saveConvoGoal(BotType.GOAL_WITHDRAW, goal);
        startBot(BotType.GOAL_WITHDRAW);
    }

    @OnClick(R.id.fragment_target_savings_plan_edit)
    public void onEditClick(View view) {
        Goal goal = goals.get(viewPager.getCurrentItem());
        persisted.saveConvoGoal(BotType.GOAL_EDIT, goal);
        startBot(BotType.GOAL_EDIT);
    }

    @OnClick(R.id.fragment_target_add_goal_button)
    public void onAddGoalClick(View view) {
        startBot(BotType.GOAL_ADD);
    }

    private void populateGoal(Goal goal) {
        goalName.setText(goal.getName());
        saved.setText(CurrencyHelper.format(goal.getValue()));
        total.setText(String.format(of_goal_total + " %s", CurrencyHelper.format(goal.getTarget())));
        int weeks = (int) WeekCalc.weekDiff(
                goal.getStartDate().toDate(),
                goal.getEndDate().toDate(),
                WeekCalc.Rounding.DOWN
        );
        int days = WeekCalc.remainder(goal.getStartDate().toDate(), goal.getEndDate().toDate());
        goalMessage.setText(String.format(savingsMessage,
                CurrencyHelper.format(goal.getWeeklyTarget()), CurrencyHelper.format(goal.getTarget()), weeks, days));

        // Goals can't go into overdraught
        withdrawButton.setEnabled(goal.canWithdraw());
    }

    private void retrieveGoals() {
        showLoadingProgress();
        goalManager.retrieveGoals(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), error_retrieving_goals, Toast.LENGTH_SHORT).show();
                        showNoGoals();
                    }
                });
            }
        }).subscribe(new Action1<List<Goal>>() {
            @Override
            public void call(final List<Goal> goals) {
                TargetFragment.this.goals = goals;
                if (getActivity() != null && goals != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateGoals(goals);
                            if (goals.size() > 0) {
                                viewPager.setCurrentItem(0);
                                populateGoal(goals.get(0));
                                updateNavCarets();
                                showGoals();
                            } else {
                                showNoGoals();
                            }
                        }
                    });
            }
        });
    }

    private void showLoadingProgress() {
        nestedScrollView.setVisibility(View.GONE);
        noGoalView.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.VISIBLE);
    }

    private void showGoals() {
        nestedScrollView.setVisibility(View.VISIBLE);
        noGoalView.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);
    }

    private void showNoGoals() {
        nestedScrollView.setVisibility(View.GONE);
        noGoalView.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.GONE);
    }

    public void refreshGoals() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    retrieveGoals();
                }
            });
        }
    }

    private void updateNavCarets() {
        int position = viewPager.getCurrentItem();
        if (position == 0)
            leftTarget.setVisibility(View.GONE);
        else
            leftTarget.setVisibility(View.VISIBLE);

        if (position == goals.size() - 1)
            rightTarget.setVisibility(View.GONE);
        else
            rightTarget.setVisibility(View.VISIBLE);
    }
}
