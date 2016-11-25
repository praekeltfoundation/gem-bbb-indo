package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.FreeformChallenge;
import org.gem.indo.dooit.models.challenge.FreeformChallengeQuestion;
import org.gem.indo.dooit.models.challenge.ParticipantFreeformAnswer;

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
    private static final String TAG = "ChallengeFreeform";
    private static final String ARG_CHALLENGE = "challenge";

    @Inject
    ChallengeManager challengeManager;
    @Inject
    Persisted persisted;

    private FreeformChallenge challenge;
    private FreeformChallengeQuestion question;

    @BindView(R.id.fragment_challenge_container)
    View background;

    @BindView(R.id.fragment_challenge_freeform_title)
    TextView title;

    @BindView(R.id.fragment_challenge_freeform_submission)
    EditText submissionBox;

    @BindView(R.id.fragment_challenge_freeform_submitbutton)
    Button submitButton;

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
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challenge_freeform, container, false);
        ButterKnife.bind(this, view);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, background);
        title.setText(question != null ? question.getText() : getString(R.string.challenge_no_question));
        fetchAnswer();
        return view;
    }

    public void fetchAnswer() {
        challengeManager.fetchParticipantFreeformAnswer(challenge.getId(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                if (error.getErrorResponse().getStatus() == 404) {
                    Log.d(TAG, "No entry submitted yet");
                } else {
                    Log.d(TAG, "Could not fetch challenge entry");
                }
            }
        }).subscribe(new Action1<ParticipantFreeformAnswer>() {
            @Override
            public void call(final ParticipantFreeformAnswer answer) {
                if (answer != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submissionBox.setText(answer.getText());
                        }
                    });
                }
            }
        });
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
                Log.d(TAG, "Could not submit challenge entry");
            }
        }).subscribe(new Action1<ParticipantFreeformAnswer>() {
            @Override
            public void call(ParticipantFreeformAnswer answer) {
                Log.d(TAG, "Entry submitted");
            }
        });
    }

    @OnClick(R.id.fragment_challenge_freeform_submitbutton)
    public void submitFreeformAnswer() {
        Toast.makeText(getContext(), org.gem.indo.dooit.R.string.challenge_freeform_submit, Toast.LENGTH_SHORT).show();
        submitAnswer(submissionBox.getText().toString());
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = ChallengeNoneFragment.newInstance();
        ft.replace(R.id.fragment_challenge_container, fragment);
        ft.commit();
    }
}
