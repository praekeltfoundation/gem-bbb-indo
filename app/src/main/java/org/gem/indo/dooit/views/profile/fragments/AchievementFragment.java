package org.gem.indo.dooit.views.profile.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AchievementManager;
import org.gem.indo.dooit.api.responses.AchievementResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.profile.adapters.BadgeAdapter;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public class AchievementFragment extends Fragment {

    @BindView(R.id.fragment_profile_current_streak_value)
    TextView streakView;

    @BindView(R.id.fragment_profile_achievement_container)
    View containerView;

    @BindView(R.id.fragment_profile_achievements_error)
    TextView errorView;

    @BindView(R.id.fragment_profile_achievement_badge_recycler_view)
    RecyclerView achievementRecyclerView;

    @BindView(R.id.fragment_achievements_progress_container)
    View progressView;

    @BindString(R.string.profile_achievements_week_streak_singular)
    String streakSingular;

    @BindString(R.string.profile_achievements_week_streak_plural)
    String streakPlural;

    @BindString(R.string.profile_error_retrieve_achievements)
    String errorRetrieveMsg;

    @Inject
    Persisted persisted;

    @Inject
    AchievementManager achievementManager;

    private BadgeAdapter adapter;

    public AchievementFragment() {

    }

    public static Fragment newInstance() {
        AchievementFragment fragment = new AchievementFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_achievements, container, false);
        ButterKnife.bind(this, view);

        // Achievements
        setStreak(0);

        adapter = new BadgeAdapter();

        Context context = getContext();
        if (context != null) {
            LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            DividerItemDecoration separator = new DividerItemDecoration(context, manager.getOrientation());
            achievementRecyclerView.setLayoutManager(manager);
            achievementRecyclerView.addItemDecoration(separator);
            achievementRecyclerView.setAdapter(adapter);
        }

        User user = persisted.getCurrentUser();
        if (user != null) {
            showProgress();
            achievementManager.retrieveAchievement(user.getId(), new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {
                    showError();
                }
            }).subscribe(new Action1<AchievementResponse>() {
                @Override
                public void call(final AchievementResponse response) {
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setStreak(response.getWeeklyStreak());
                                adapter.addAll(response.getBadges());
                                showContainer();
                            }
                        });
                }
            });
        } else {
            showError();
        }

        return view;
    }

    ////////
    // UI //
    ////////

    protected void setStreak(int streak) {
        if (streak == 1)
            streakView.setText(String.format(streakSingular, streak));
        else
            streakView.setText(String.format(streakPlural, streak));
    }

    protected void showContainer() {
        progressView.setVisibility(View.GONE);
        containerView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    protected void showProgress() {
        progressView.setVisibility(View.VISIBLE);
        containerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    protected void showError() {
        progressView.setVisibility(View.GONE);
        containerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errorRetrieveMsg);

        Context context = getContext();
        if (context != null)
            Toast.makeText(context, errorRetrieveMsg, Toast.LENGTH_SHORT).show();
    }
}
