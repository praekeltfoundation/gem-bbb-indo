package org.gem.indo.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeNoneFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class ChallengeFragment extends Fragment {

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    @BindView(R.id.fragment_challenge_loadingprogress)
    ProgressBar progressBar;

    @BindView(R.id.fragment_challenge_loadingtext)
    TextView progressText;

    BaseChallenge challenge;

    Unbinder unbinder;
    private Observable<List<BaseChallenge>> challengeSubscription;

    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        Fragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challenge_loading, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
////        if (persisted.hasCurrentChallenge()) {
////            challenge = (BaseChallenge) persisted.getCurrentChallenge();
////            startChallenge();
////        }
        return view;
    }

    @Override
    public void onDestroyView() {
        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        challengeSubscription = challengeManager.retrieveChallenges(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Could not retrieve challenges", Toast.LENGTH_SHORT).show();
            }
        });

        challengeSubscription
                .subscribe(new Action1<List<BaseChallenge>>() {
                    @Override
                    public void call(List<BaseChallenge> challenges) {
                        if (challenges.size() > 0) {
                            challenge = challenges.get(0);
                            persisted.setActiveChallenge(challenge);

                            if (getActivity().findViewById(R.id.fragment_challenge_container) != null) {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                Fragment fragment = ChallengeRegisterFragment.newInstance();
                                Bundle args = new Bundle();
                                args.putParcelable("challenge", challenge);
                                fragment.setArguments(args);
                                ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
                                ft.commit();
                            }
                        } else {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Fragment fragment = ChallengeNoneFragment.newInstance();
                            ft.replace(R.id.fragment_challenge_container, fragment);
                            ft.commit();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);

                            }
                        });
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (challengeSubscription != null)
            challengeSubscription.doOnUnsubscribe(new Action0() {
                @Override
                public void call() {

                }
            });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main_challenge, menu);
    }
}
