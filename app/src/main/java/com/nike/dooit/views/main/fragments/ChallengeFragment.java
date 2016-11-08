package com.nike.dooit.views.main.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.ChallengeManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.Challenge;
import com.nike.dooit.views.main.fragments.challenge.ChallengeQuizQuestionFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class ChallengeFragment extends Fragment {

    @BindView(R.id.fragment_challenge_image_image_view)
    ImageView topImage;

    @BindView(R.id.fragment_challenge_sub_title_text_view)
    TextView title;

    @BindView(R.id.fragment_challenge_name_text_view)
    TextView name;

    @BindView(R.id.fragment_challenge_expire_date_text_view)
    TextView date;

    @BindView(R.id.fragment_challenge_instruction_text_vew)
    TextView instruction;

    @BindView(R.id.fragment_challenge_t_c_text_view)
    TextView tc;

    @BindView(R.id.fragment_challenge_register_button)
    Button register;

    ProgressBar progressBar;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    Challenge challenge;

    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
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
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        ButterKnife.bind(this, view);
        progressBar = (ProgressBar) view.getRootView().findViewById(R.id.fragment_challenge_progress_progress_bar);

//        if (persisted.hasCurrentChallenge()) {
//            challenge = (Challenge) persisted.getCurrentChallenge();
//            startChallenge();
//        }
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
        }).subscribe(new Action1<List<Challenge>>() {
            @Override
            public void call(List<Challenge> challenges) {
                if (challenges.size() > 0) {
                    challenge = challenges.get(0);
                    persisted.setActiveChallenge(challenge);

                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(challenge.getName());

                            }
                        });

                }
            }
        });
    }

    @OnClick(R.id.fragment_challenge_register_button)
    void startChallenge() {
        System.out.println("Starting challenge");
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ChallengeQuizQuestionFragment fragment = ChallengeQuizQuestionFragment.newInstance();

//        if (challenge != null) {
            System.out.println("Passing question arg");
            Bundle args = new Bundle();
            args.putParcelableArrayList("questions", new ArrayList<Parcelable>(challenge.getQuestions()));
            fragment.setArguments(args);
//        }

        //ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");

        ft.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_challenge, menu);
    }
}
