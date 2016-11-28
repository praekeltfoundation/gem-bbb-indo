package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.FreeformChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeRegisterFragment extends Fragment {
    private static final String TAG = "ChallengeRegister";
    private static final String ARG_CHALLENGE = "challenge";

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persist;

    @BindView(R.id.fragment_challenge_container)
    View background;

    @BindView(R.id.fragment_challenge_register_image)
    SimpleDraweeView topImage;

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

    private BaseChallenge challenge;
    private Observable<Participant> participantSubscription = null;
    private Unbinder unbinder = null;

    public ChallengeRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChallengeRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeRegisterFragment newInstance() {
        ChallengeRegisterFragment fragment = new ChallengeRegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challenge = getArguments().getParcelable(ARG_CHALLENGE);
        }
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challenge_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, background);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(challenge.getName());
        date.setText(challenge.getDeactivationDate().toLocalDateTime().toString("yyyy-MM-dd HH:mm"));
        topImage.setImageURI(challenge.getImageURL());
    }

    private Fragment startQuizChallenge(Participant participant, QuizChallenge quizChallenge) {
        if (quizChallenge.getQuestions() == null || quizChallenge.getQuestions().size() <= 0) {
            return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_questions));
        }

        for (QuizChallengeQuestion q : quizChallenge.getQuestions()) {
            // check for empty question or empty list of options for question
            if (q.getText() == null || q.getText().length() <= 0) {
                return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_questions));
            } else if (q.getOptions() == null || q.getOptions().size() <= 0) {
                return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_questions));
            }

            // check whether any options are empty or none of the question's options are correct
            boolean hasCorrect = false;
            for (QuizChallengeOption o : q.getOptions()) {
                if (o.getText() == null || o.getText().length() <= 0) {
                    return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_empty_option));
                }
                hasCorrect |= o.getCorrect();
            }
            if (!hasCorrect) {
                return ChallengeNoneFragment.newInstance(getString(org.gem.indo.dooit.R.string.challenge_quiz_no_correct_answer));
            }
        }
        return ChallengeQuizFragment.newInstance(participant, quizChallenge);
    }

    private Fragment startFreeformChallenge(Participant participant, FreeformChallenge freeformChallenge) {
        if (freeformChallenge.getQuestion() == null) {
            return ChallengeNoneFragment.newInstance("Freeform challenge has no question.");
        } else if (freeformChallenge.getQuestion().getText() == null ||
                freeformChallenge.getQuestion().getText().isEmpty()) {
            return ChallengeNoneFragment.newInstance("Freeform challenge question is empty.");
        }
        return ChallengeFreeformFragment.newInstance(participant, freeformChallenge);
    }

    @OnClick(R.id.fragment_challenge_register_button)
    void registerClick() {
        Participant participant = new Participant();
        participant.setChallenge(challenge.getId());

        participantSubscription = challengeManager.registerParticipant(
            participant,
            new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {
                    Toast.makeText(getContext(), "Could not confirm registration", Toast.LENGTH_SHORT).show();
                }
            }
        );

        participantSubscription.subscribe(new Action1<Participant>() {
            @Override
            public void call(Participant participant1) {
                persist.setActiveChallenge(challenge);
                startChallenge(participant1);
            }
        });
    }

    void startChallenge(Participant participant) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (challenge.getType()) {
            case QUIZ:
                if (challenge instanceof QuizChallenge) {
                    fragment = startQuizChallenge(participant, (QuizChallenge) challenge);
                }
                break;
            case FREEFORM:
                if (challenge instanceof FreeformChallenge) {
                    fragment = startFreeformChallenge(participant, (FreeformChallenge) challenge);
                }
                break;
            default:
                throw new RuntimeException("Invalid challenge type provided");
        }

        if (fragment != null) {
            //ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
            ft.commit();
        }
    }
}
