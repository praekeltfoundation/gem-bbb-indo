package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.FreeformChallenge;
import org.gem.indo.dooit.models.challenge.FreeformChallengeQuestion;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.ParticipantFreeformAnswer;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeActivity;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentMainPage;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentState;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeFreeformFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFreeformFragment extends Fragment {

    /*************
     * Variables *
     *************/

    private static final String TAG = "ChallengeFreeform";
    private static final String ARG_ANSWER = "challenge_freeform_answer";
    private static final ChallengeFragmentState FRAGMENT_STATE = ChallengeFragmentState.FREEFORM;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    private FreeformChallenge challenge;

    private FreeformChallengeQuestion question;

    private Participant participant;

    private Unbinder unbinder = null;

    private Subscription submissionSubscription = null;

    @BindView(R.id.fragment_challenge_freeform_title)
    TextView title;

    @BindView(R.id.fragment_challenge_freeform_submission)
    EditText submissionBox;

    @BindView(R.id.fragment_challenge_freeform_submitbutton)
    Button submitButton;

    @BindView(R.id.fragment_challenge_close)
    ImageButton close;

    /****************
     * Constructors *
     ****************/

    public ChallengeFreeformFragment() {
        // Required empty public constructor
    }

    public static ChallengeFreeformFragment newInstance(Participant participant, FreeformChallenge challenge) {
        ChallengeFreeformFragment fragment = new ChallengeFreeformFragment();
        Bundle args = new Bundle();
        args.putParcelable(ChallengeFragment.ARG_PARTICIPANT, participant);
        args.putParcelable(ChallengeFragment.ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }


    /************************
     * Life-cycle overrides *
     ************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        if (getArguments() != null) {
            participant = persisted.getParticipant();
            //participant = getArguments().getParcelable(ChallengeFragment.ARG_PARTICIPANT);
            challenge = getArguments().getParcelable(ChallengeFragment.ARG_CHALLENGE);
            question = challenge != null ? challenge.getQuestion() : null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_freeform, container, false);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, view);
        unbinder = ButterKnife.bind(this, view);
        title.setText(question != null ? question.getText() : getString(R.string.challenge_no_question));
        if (savedInstanceState == null) {
            fetchAnswer();
        } else {
            Log.d(TAG, "Restoring state from bundle");
            String answerText = savedInstanceState.getString(ARG_ANSWER);
            submissionBox.setText(answerText == null ? "" : answerText);
        }
        return view;
    }

    @Override
    public void onStop() {
        if (submissionSubscription != null) {
            submissionSubscription.unsubscribe();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    /****************
     * Load helpers *
     ****************/

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


    /******************
     * Submit helpers *
     ******************/
    @OnClick(R.id.fragment_challenge_freeform_submitbutton)
    public void submitAnswer() {
        ParticipantFreeformAnswer answer = new ParticipantFreeformAnswer();

        answer.setParticipant(persisted.getParticipant().getId());
        answer.setQuestion(question.getId());
        answer.setText(submissionBox.getText().toString());

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.label_loading));
        dialog.show();

        submissionSubscription = challengeManager.createParticipantFreeformAnswer(answer, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Log.d(TAG, "Could not submit challenge entry");
            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                dialog.dismiss();
            }
        }).subscribe(new Action1<ParticipantFreeformAnswer>() {
            @Override
            public void call(ParticipantFreeformAnswer answer) {
                Log.d(TAG, "Entry submitted");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putParcelable(ChallengeActivity.ARG_PARTICIPANT_BADGE, answer.getBadge());
                args.putParcelable(ChallengeActivity.ARG_CHALLENGE,challenge);
                args.putParcelable(ChallengeActivity.ARG_PARTICIPANT,participant);

                Fragment fragment = ChallengeDoneFragment.newInstance(challenge);
                fragment.setArguments(args);
                ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
                ft.commit();

            }
        });
        persisted.clearCurrentChallenge();
    }

    @OnClick(R.id.fragment_challenge_close)
    public void closeQuiz(){
        returnToParent(null);
    }

    private void returnToParent(ChallengeFragmentMainPage returnPage) {
        persisted.setParticipant(participant);

        FragmentActivity activity = getActivity();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChallengeActivity.ARG_CHALLENGE,challenge);
        bundle.putInt(ChallengeActivity.ARG_RETURNPAGE, returnPage != null ? returnPage.ordinal() : -1);
        intent.putExtras(bundle);
        this.onSaveInstanceState(bundle);

        if (activity.getParent() == null) {
            activity.setResult(Activity.RESULT_OK, intent);
        } else {
            activity.getParent().setResult(Activity.RESULT_OK, intent);
        }
        activity.finish();
    }

    /*************************
     * State-keeping methods *
     *************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putSerializable(ChallengeFragment.ARG_PAGE, FRAGMENT_STATE);
            persisted.setParticipant(participant);
            outState.putParcelable(ChallengeFragment.ARG_CHALLENGE, challenge);
            outState.putString(ARG_ANSWER, submissionBox == null ? "" : submissionBox.toString());
        }
        super.onSaveInstanceState(outState);
    }
}
