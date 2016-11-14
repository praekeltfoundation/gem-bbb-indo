package com.nike.dooit.views.main.fragments.challenge.fragments;

import android.os.Bundle;
import android.os.DropBoxManager;
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

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.ChallengeManager;
import com.nike.dooit.api.managers.UserManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.challenge.ParticipantAnswer;
import com.nike.dooit.models.challenge.QuizChallenge;
import com.nike.dooit.models.challenge.QuizChallengeEntry;
import com.nike.dooit.models.challenge.QuizChallengeOption;
import com.nike.dooit.models.challenge.QuizChallengeQuestion;
import com.nike.dooit.views.main.fragments.challenge.adapters.ChallengeQuizPagerAdapter;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionSelectedListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    public static ChallengeQuizFragment newInstance(QuizChallenge challenge) {
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
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        mAdapter = new ChallengeQuizPagerAdapter(getChildFragmentManager(), getContext(), mChallenge);
        mAdapter.setOnOptionChangeListener(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_quiz, container, false);
        ButterKnife.bind(this, view);
        mPager.setAdapter(mAdapter);
        mProgressCounter.setText(String.format("Question %d/%d", mPager.getCurrentItem(), mChallenge.getQuestions().size()));
        return view;
    }

    @OnClick(R.id.fragment_challenge_quiz_checkbutton) public void checkAnswer() {
        if (currentOption == null) {
            Toast.makeText(getContext(), "You must select an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(
                getContext(),
                String.format("Q: \"%s\"; A: \"%s\"",
                currentQuestion != null ? currentQuestion.getText() : "<NONE>",
                currentOption != null ? currentOption.getText() : "<NONE>"),
                Toast.LENGTH_SHORT).show();

        if (currentOption.getCorrect()) {
            Toast.makeText(getContext(), "A winner is you", Toast.LENGTH_SHORT).show();
            captureAnswer(currentQuestion, currentOption);
            nextQuestion();
        } else {
            Toast.makeText(getContext(), "My name is Error", Toast.LENGTH_SHORT).show();
            captureAnswer(currentQuestion, currentOption);
        }
    }

    @Override public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
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
