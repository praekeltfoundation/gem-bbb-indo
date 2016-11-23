package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import org.gem.indo.dooit.helpers.TilingDrawable;
import org.gem.indo.dooit.models.challenge.ParticipantAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeEntry;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.views.main.fragments.challenge.adapters.ChallengeQuizPagerAdapter;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnQuestionCompletedListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import butterknife.OnPageChange;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * A {@link Fragment} subclass. All child fragments register listeners for quiz events here.
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
    private Set<OnOptionChangeListener> optionChangeListeners = new HashSet<>();
    private Set<OnQuestionCompletedListener> questionCompletedListeners = new HashSet<>();
    private Map<Long, QuestionState> selections = new HashMap<>();

    @BindView(R.id.fragment_chalenge_nested_bg)
    View background;
    @BindView(R.id.fragment_challenge_quiz_progressbar)
    ProgressBar mProgressBar;
    @BindView(org.gem.indo.dooit.R.id.fragment_challenge_quiz_progresscounter)
    TextView mProgressCounter;
    @BindView(org.gem.indo.dooit.R.id.fragment_challenge_quiz_pager)
    ViewPager mPager;
    @BindView(org.gem.indo.dooit.R.id.fragment_challenge_quiz_checkbutton)
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
        mAdapter = new ChallengeQuizPagerAdapter(this, getChildFragmentManager(), mChallenge);
        addOptionChangeListener(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challenge_quiz, container, false);
        unbinder = ButterKnife.bind(this, view);
        ShapeDrawable back = new ShapeDrawable();
        back.getPaint().setColor(ContextCompat.getColor(getContext(), R.color.grey_back));
        Drawable fore = ContextCompat.getDrawable(getContext(), R.drawable.bkg_clipped);
        DrawableCompat.setTint(fore, ContextCompat.getColor(getContext(), R.color.grey_fore));
        LayerDrawable layers = new LayerDrawable(new Drawable[]{back, fore});
        TilingDrawable tiled = new TilingDrawable(layers);
        background.setBackground(tiled);
        mPager.setAdapter(mAdapter);
        updateProgressCounter(0);
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
    public void onDestroy() {
        removeQuestionCompletedListener(mAdapter);
        super.onDestroy();
    }

    @OnClick(org.gem.indo.dooit.R.id.fragment_challenge_quiz_checkbutton)
    public void checkAnswer() {
        if (mPager.getCurrentItem() >= mChallenge.numQuestions()) {
            submitParticipantEntry();
            return;
        }

        if (currentOption == null) {
            Toast.makeText(getContext(), org.gem.indo.dooit.R.string.challenge_quiz_select_option_required, Toast.LENGTH_SHORT).show();
            return;
        }

        ChallengeSuccessLightboxFragment lightbox = null;
        if (currentOption.getCorrect()) {
            captureAnswer(currentQuestion, currentOption);
            setCompleted(currentQuestion.getId());
            lightbox = ChallengeSuccessLightboxFragment.newInstance(true);
            lightbox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    nextQuestion();
                    updateProgressBar();
                }
            });
        } else {
            captureAnswer(currentQuestion, currentOption);
            lightbox = ChallengeSuccessLightboxFragment.newInstance(false);
        }
        lightbox.show(getActivity().getSupportFragmentManager(), "challenge_lightbox");
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
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = ChallengeNoneFragment.newInstance("Thank you for participating!");
                ft.replace(org.gem.indo.dooit.R.id.fragment_challenge_container, fragment, "fragment_challenge");
                ft.commit();
            }
        });
    }

    public void nextQuestion() {
        currentOption = null;
        int idx = mPager.getCurrentItem() + 1;
        if (idx < mPager.getChildCount()) {
            mPager.setCurrentItem(idx);
        } else {
            Toast.makeText(getContext(), org.gem.indo.dooit.R.string.challenge_all_questions_complete, Toast.LENGTH_SHORT).show();
            submitParticipantEntry();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = ChallengeNoneFragment.newInstance();
            ft.replace(org.gem.indo.dooit.R.id.fragment_challenge_container, fragment, "fragment_challenge");
            ft.commit();
        }
    }

    private void updateProgressCounter(int position) {
        if (mProgressCounter == null) {
            return;
        }

        if (position < mChallenge.numQuestions()) {
            mProgressCounter.setText(String.format(getString(org.gem.indo.dooit.R.string.challenge_quiz_counter_fmt), position + 1, mChallenge.numQuestions()));
        } else {
            mProgressCounter.setText("");
        }
    }

    private void updateProgressBar() {
        if (mProgressBar == null) {
            return;
        }

        mProgressBar.setProgress(100 * numCompleted() / mChallenge.numQuestions());
    }

    @OnPageChange(org.gem.indo.dooit.R.id.fragment_challenge_quiz_pager)
    public void onPageSelected(int position) {
        Log.d(TAG, "Page change: " + String.valueOf(position));
        updateProgressCounter(position);
        if (position == mAdapter.getCount() - 1) {
            checkButton.setText(org.gem.indo.dooit.R.string.label_done);
        } else {
            checkButton.setText(org.gem.indo.dooit.R.string.label_check_result);
        }
    }

    public void addOptionChangeListener(OnOptionChangeListener listener) {
        optionChangeListeners.add(listener);
    }

    public void removeOptionChangeListener(OnOptionChangeListener listener) {
        optionChangeListeners.remove(listener);
    }

    @Override
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
        currentQuestion = question;
        currentOption = option;
        long questionId = question.getId();
        selections.put(questionId, new QuestionState(option.getId(), isCompleted(questionId)));
        for (OnOptionChangeListener l: optionChangeListeners) {
            if (l != null) {
                l.onOptionChange(question, option);
            }
        }
    }

    public void addQuestionCompletedListener(OnQuestionCompletedListener listener) {
        questionCompletedListeners.add(listener);
    }

    public void removeQuestionCompletedListener(OnQuestionCompletedListener listener) {
        questionCompletedListeners.remove(listener);
    }

    public void setCompleted(long questionId) {
        if (selections.containsKey(questionId)) {
            selections.put(questionId, new QuestionState(selections.get(questionId).optionId, true));
        }
        for (OnQuestionCompletedListener l: questionCompletedListeners) {
            if (l != null) {
                l.onQuestionCompleted(questionId);
            }
        }
    }

    public boolean isCompleted(long questionId) {
        return selections.containsKey(questionId) && selections.get(questionId).completed;
    }

    public long getSelectedOption(long questionId) {
        return selections.containsKey(questionId) ? selections.get(questionId).optionId : -1;
    }

    public int numCompleted() {
        int total = 0;
        for (QuestionState state: selections.values()) {
            total += state.completed ? 1 : 0;
        }
        return total;
    }

    private class QuestionState {
        private long optionId = -1;
        private boolean completed = false;

        QuestionState(long optionId, boolean completed) {
            this.optionId = optionId;
            this.completed = completed;
        }
    }
}
