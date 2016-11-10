package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.dooit.R;
import com.nike.dooit.models.Challenge;
import com.nike.dooit.views.main.fragments.challenge.adapters.ChallengeQuizPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeQuizFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    private Challenge mChallenge;
    private ChallengeQuizPagerAdapter mAdapter;

    @BindView(R.id.fragment_challenge_quiz_progressbar) ProgressBar mProgressBar;
    @BindView(R.id.fragment_challenge_quiz_progresscounter) TextView mProgressCounter;
    @BindView(R.id.fragment_challenge_quiz_pager) ViewPager mPager;
    @BindView(R.id.fragment_challenge_quiz_checkbutton) Button checkButton;

    public ChallengeQuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param challenge Quiz type challenge.
     * @return A new instance of fragment ChallengeQuizFragment.
     */
    public static ChallengeQuizFragment newInstance(Challenge challenge) {
        ChallengeQuizFragment fragment = new ChallengeQuizFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChallenge = getArguments().getParcelable(ARG_CHALLENGE);
        }
        mAdapter = new ChallengeQuizPagerAdapter(getChildFragmentManager(), getContext(), mChallenge);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_quiz, container, false);
        ButterKnife.bind(this, view);
        mPager.setAdapter(mAdapter);
        mProgressCounter.setText(String.format("Question 1/%d", mChallenge.getQuestions().size()));
        return view;
    }

    @OnClick(R.id.fragment_challenge_quiz_checkbutton) public void checkAnswer() {
        Toast.makeText(getContext(), "Checking answer", Toast.LENGTH_SHORT).show();
    }
}
