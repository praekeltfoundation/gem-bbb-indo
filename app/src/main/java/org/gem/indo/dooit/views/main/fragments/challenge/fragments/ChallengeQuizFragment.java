package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.challenge.ParticipantAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeEntry;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.views.main.fragments.challenge.adapters.ChallengeQuizPagerAdapter;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeQuizFragment extends Fragment implements OnOptionChangeListener {
    private static final String TAG = "ChallengeQuiz";
    private static final String ARG_CHALLENGE = "challenge";

    @Inject
    Persisted persisted;
    @Inject
    ChallengeManager challengeManager;
    private QuizChallenge mChallenge;
    private ChallengeQuizPagerAdapter mAdapter;
    private QuizChallengeQuestion currentQuestion = null;
    private QuizChallengeOption currentOption = null;
    private List<ParticipantAnswer> answers = new ArrayList<ParticipantAnswer>();
    private Unbinder unbinder = null;

    @BindView(R.id.fragment_challenge_quiz_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.fragment_challenge_quiz_progresscounter)
    TextView mProgressCounter;
    @BindView(R.id.fragment_challenge_quiz_pager)
    ViewPager mPager;
    @BindView(R.id.fragment_challenge_quiz_checkbutton)
    Button checkButton;

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
    public static ChallengeQuizFragment newInstance(QuizChallenge challenge) {
        ChallengeQuizFragment fragment = new ChallengeQuizFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChallenge = getArguments().getParcelable(ARG_CHALLENGE);
        }
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        mAdapter = new ChallengeQuizPagerAdapter(getChildFragmentManager(), getContext(), mChallenge);
        mAdapter.setOnOptionChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_quiz, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPager.setAdapter(mAdapter);
        mProgressCounter.setText(String.format("Question %d/%d", mPager.getCurrentItem(), mChallenge.getQuestions().size()));
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @OnClick(R.id.fragment_challenge_quiz_checkbutton)
    public void checkAnswer() {
        if (currentOption == null) {
            Toast.makeText(getContext(), R.string.challenge_quiz_select_option_required, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(
                getContext(),
                String.format("Q: \"%s\"; A: \"%s\"",
                currentQuestion != null ? currentQuestion.getText() : "<NONE>",
                currentOption != null ? currentOption.getText() : "<NONE>"),
                Toast.LENGTH_SHORT).show();

        if (currentOption.getCorrect()) {
            Toast.makeText(getContext(), R.string.challenge_quiz_congratulate_correct, Toast.LENGTH_SHORT).show();
            captureAnswer(currentQuestion, currentOption);
            nextQuestion();
        } else {
            Toast.makeText(getContext(), R.string.challenge_quiz_sorry_incorrect, Toast.LENGTH_SHORT).show();
            captureAnswer(currentQuestion, currentOption);
        }
    }

    @Override
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
        currentQuestion = question;
        currentOption = option;
    }

    public void captureAnswer(QuizChallengeQuestion question, QuizChallengeOption option) {
        answers.add(new ParticipantAnswer(persisted.getCurrentUser().getId(), mChallenge.getId(), question.getId(), option.getId()));
    }

    public void submitParticipantEntry() {
        QuizChallengeEntry entry = new QuizChallengeEntry();
        entry.setUser(persisted.getCurrentUser().getId());
        entry.setChallenge(mChallenge.getId());
        entry.setDateCompleted(new DateTime());
        entry.setAnswers(answers);
        challengeManager.createEntry(entry, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Log.d(TAG, "Could not submit challenge entry");
            }
        }).subscribe(new Action1<QuizChallengeEntry>() {
            @Override
            public void call(QuizChallengeEntry entry) {
                Log.d(TAG, "Entry submitted");
            }
        });
    }

    public void nextQuestion() {
        currentOption = null;
        int idx = mPager.getCurrentItem() + 1;
        if (idx < mPager.getChildCount()) {
            mPager.setCurrentItem(idx + 1);
        } else {
            Toast.makeText(getContext(), R.string.challenge_all_questions_complete, Toast.LENGTH_SHORT).show();
            submitParticipantEntry();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = ChallengeNoneFragment.newInstance();
            ft.replace(R.id.fragment_challenge_container, fragment);
            ft.commit();
        }
    }
}
