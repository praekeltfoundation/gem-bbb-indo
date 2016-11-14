package com.nike.dooit.views.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.ChallengeManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.challenge.BaseChallenge;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeNoneFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.functions.Action1;

public class ChallengeFragment extends Fragment {

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    BaseChallenge challenge;

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
        View view = inflater.inflate(R.layout.fragment_challenge_loading, container, false);
        ButterKnife.bind(this, view);

////        if (persisted.hasCurrentChallenge()) {
////            challenge = (BaseChallenge) persisted.getCurrentChallenge();
////            startChallenge();
////        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        challengeManager.retrieveChallenges(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Could not retrieve challenges", Toast.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<List<BaseChallenge>>() {
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
                    ft.replace(R.id.fragment_challenge_container, fragment);
                    ft.commit();
                }
            } else {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = ChallengeNoneFragment.newInstance();
                ft.replace(R.id.fragment_challenge_container, fragment);
                ft.commit();
            }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_challenge, menu);
    }
}
