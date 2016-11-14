package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.ChallengeManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.challenge.FreeformChallenge;
import com.nike.dooit.models.challenge.FreeformChallengeQuestion;
import com.nike.dooit.models.challenge.ParticipantFreeformAnswer;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeFreeformFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFreeformFragment extends Fragment {
    private static final String ARG_CHALLENGE = "challenge";

    @Inject
    ChallengeManager challengeManager;
    @Inject
    Persisted persisted;
    private FreeformChallenge challenge;
    private FreeformChallengeQuestion question;

    @BindView(R.id.fragment_challenge_freeform_title) TextView title;
    @BindView(R.id.fragment_challenge_freeform_submission) EditText submissionBox;
    @BindView(R.id.fragment_challenge_freeform_submitbutton) Button submitButton;

    public ChallengeFreeformFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param challenge Freeform type challenge.
     * @return A new instance of fragment ChallengeFreeformFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeFreeformFragment newInstance(FreeformChallenge challenge) {
        ChallengeFreeformFragment fragment = new ChallengeFreeformFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        if (getArguments() != null) {
            challenge = getArguments().getParcelable(ARG_CHALLENGE);
            question = challenge.getQuestion();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_freeform, container, false);
        ButterKnife.bind(this, view);
        title.setText(question != null ? question.getText() : "No question");
        return view;
    }

    public void submitAnswer(String text) {
        ParticipantFreeformAnswer answer = new ParticipantFreeformAnswer();
        answer.setUser(persisted.getCurrentUser().getId());
        answer.setChallenge(challenge.getId());
        answer.setQuestion(question.getId());
        answer.setText(text);
        challengeManager.createParticipantFreeformAnswer(answer, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
//                Toast.makeText(getContext(), "Could not submit challenge entry", Toast.LENGTH_SHORT).show();
                System.out.println("Could not submit challenge entry");
            }
        }).subscribe(new Action1<ParticipantFreeformAnswer>() {
            @Override
            public void call(ParticipantFreeformAnswer answer) {
//                Toast.makeText(getContext(), "Entry submitted", Toast.LENGTH_SHORT).show();
                System.out.println("Entry submitted");
            }
        });
    }

    @OnClick(R.id.fragment_challenge_freeform_submitbutton)
    public void submitFreeformAnswer() {
        Toast.makeText(getContext(), "Submitting freeform answer", Toast.LENGTH_SHORT).show();
        submitAnswer(submissionBox.getText().toString());
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = ChallengeNoneFragment.newInstance();
        ft.replace(R.id.fragment_challenge_container, fragment);
        ft.commit();
    }
}
