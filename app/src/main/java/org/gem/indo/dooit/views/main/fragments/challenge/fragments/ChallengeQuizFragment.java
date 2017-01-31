package org.gem.indo.dooit.views.main.fragments.challenge.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.ParticipantAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeEntry;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestionState;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeActivity;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentMainPage;
import org.gem.indo.dooit.views.main.fragments.challenge.ChallengeFragmentState;
import org.gem.indo.dooit.views.main.fragments.challenge.adapters.ChallengeQuizPagerAdapter;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnQuestionCompletedListener;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * A {@link Fragment} subclass. All child fragments register listeners for quiz events here.
 * Use the {@link ChallengeQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeQuizFragment extends Fragment implements OnOptionChangeListener {
    private static final String TAG = "ChallengeQuiz";
    private static final String ARG_ANSWERS = "quiz_answers";
    private static final String ARG_STATE = "quiz_state";
    private static final ChallengeFragmentState FRAGMENT_STATE = ChallengeFragmentState.QUIZ;

    @Inject
    Persisted persisted;

    @Inject
    ChallengeManager challengeManager;

    @BindView(R.id.fragment_challenge_quiz_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.fragment_challenge_quiz_progresscounter)
    TextView mProgressCounter;

    @BindView(R.id.fragment_challenge_quiz_pager)
    ViewPager mPager;

    @BindView(R.id.fragment_challenge_quiz_checkbutton)
    Button checkButton;

    @BindView(R.id.fragment_challenge_close)
    ImageButton close;

    private boolean challengeCompleted = false;
    private ChallengeQuizPagerAdapter mAdapter;
    private List<ParticipantAnswer> answers = new ArrayList<ParticipantAnswer>();
    private LongSparseArray<QuizChallengeQuestionState> selections = new LongSparseArray<>();
    private Participant participant;
    private QuizChallenge challenge;
    private QuizChallengeOption currentOption = null;
    private QuizChallengeQuestion currentQuestion = null;
    private Set<OnOptionChangeListener> optionChangeListeners = new HashSet<>();
    private Set<OnQuestionCompletedListener> questionCompletedListeners = new HashSet<>();
    private Unbinder unbinder = null;
    private Subscription entrySubscription = null;


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
    public static ChallengeQuizFragment newInstance(Participant participant, QuizChallenge challenge) {
        ChallengeQuizFragment fragment = new ChallengeQuizFragment();
        Bundle args = new Bundle();
        args.putParcelable(ChallengeActivity.ARG_PARTICIPANT, participant);
        args.putParcelable(ChallengeActivity.ARG_CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            participant = getArguments().getParcelable(ChallengeActivity.ARG_PARTICIPANT);
            challenge = getArguments().getParcelable(ChallengeActivity.ARG_CHALLENGE);
        }
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        if (mAdapter == null) {
            mAdapter = new ChallengeQuizPagerAdapter(this, getChildFragmentManager(), challenge);
            addOptionChangeListener(mAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_quiz, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPager.setAdapter(mAdapter);
        updateProgressCounter(0);
        updateProgressBar();
        return view;
    }

    @Override
    public void onDestroy() {
        removeQuestionCompletedListener(mAdapter);
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @OnClick(R.id.fragment_challenge_close)
    public void closeQuiz() {
        onStop();
        returnToParent(null);
    }

    @OnClick(R.id.fragment_challenge_quiz_checkbutton)
    public void checkAnswer() {
        if (mPager.getCurrentItem() >= challenge.numQuestions()) {
            submitParticipantEntry();
            return;
        }

        if (currentOption == null) {
            Toast.makeText(getContext(), R.string.challenge_quiz_select_option_required, Toast.LENGTH_SHORT).show();
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
            lightbox = ChallengeSuccessLightboxFragment.newInstance(false, currentQuestion.getHint());
        }
        lightbox.show(getActivity().getSupportFragmentManager(), "challenge_lightbox");
    }

    public void captureAnswer(QuizChallengeQuestion question, QuizChallengeOption option) {
        answers.add(new ParticipantAnswer(persisted.getCurrentUser().getId(), challenge.getId(), question.getId(), option.getId()));
    }

    public void submitParticipantEntry() {
        QuizChallengeEntry entry = new QuizChallengeEntry();
        entry.setParticipant(participant.getId());
        entry.setDateCompleted(new DateTime());
        entry.setAnswers(answers);

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.label_loading));
        dialog.show();

        entrySubscription = challengeManager.createEntry(entry, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Log.d(TAG, "Could not submit challenge entry");
                returnToParent(null);
            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                dialog.dismiss();
            }
        }).subscribe(new Action1<QuizChallengeEntry>() {
            @Override
            public void call(QuizChallengeEntry entry) {
                Log.d(TAG, "Entry submitted");
                persisted.clearCurrentChallenge();

                clearState();
                challengeCompleted = true;

                Bundle args = new Bundle();
                args.putParcelable(ChallengeActivity.ARG_PARTICIPANT_BADGE, entry.getBadge());
                args.putParcelable(ChallengeActivity.ARG_CHALLENGE,challenge);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment fragment = ChallengeQuizDoneFragment.newInstance(challenge);
                fragment.setArguments(args);

                ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
                ft.commit();
            }
        });
    }

    public void nextQuestion() {
        currentOption = null;
        int idx = mPager.getCurrentItem() + 1;
        if (idx < mAdapter.getCount()) {
            mPager.setCurrentItem(idx);
        } else {
            Toast.makeText(getContext(), R.string.challenge_all_questions_complete, Toast.LENGTH_SHORT).show();
            submitParticipantEntry();
        }
    }

    private void returnToParent(ChallengeFragmentMainPage returnPage) {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChallengeActivity.ARG_CHALLENGE,challenge);
        bundle.putInt(ChallengeActivity.ARG_RETURNPAGE, returnPage != null ? returnPage.ordinal() : -1);
        persisted.setParticipant(participant);
        intent.putExtras(bundle);

        if (activity.getParent() == null) {
            activity.setResult(Activity.RESULT_OK, intent);
        } else {
            activity.getParent().setResult(Activity.RESULT_OK, intent);
        }
        activity.finish();
    }

    private void updateProgressCounter(int position) {
        if (mProgressCounter == null) {
            return;
        }

        if (position < challenge.numQuestions()) {
            mProgressCounter.setText(String.format(getString(R.string.challenge_quiz_counter_fmt), position + 1, challenge.numQuestions()));
        } else {
            mProgressCounter.setText("");
        }
    }

    private void updateProgressBar() {
        if (mProgressBar == null) {
            return;
        }
        mProgressBar.setProgress(5 + (95 * numCompleted() / challenge.numQuestions()));
    }

    @OnPageChange(R.id.fragment_challenge_quiz_pager)
    public void onPageSelected(int position) {
        Log.d(TAG, "Quiz page change: " + String.valueOf(position));
        updateProgressCounter(position);
        if (position == mAdapter.getCount() - 1) {
            checkButton.setText(R.string.challenge_done);
            submitParticipantEntry();
        } else {
            checkButton.setText(R.string.label_check_result);
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
        selections.put(questionId, new QuizChallengeQuestionState(option.getId(), isCompleted(questionId)));
        for (OnOptionChangeListener l : optionChangeListeners) {
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
        if (selections.get(questionId) != null) {
            selections.put(questionId, new QuizChallengeQuestionState(selections.get(questionId).optionId, true));
        }
        for (OnQuestionCompletedListener l : questionCompletedListeners) {
            if (l != null) {
                l.onQuestionCompleted(questionId);
            }
        }
    }

    public boolean isCompleted(long questionId) {
        return selections.get(questionId) != null && selections.get(questionId).completed;
    }

    public long getSelectedOption(long questionId) {
        return selections.get(questionId) != null ? selections.get(questionId).optionId : -1;
    }

    public int numCompleted() {
        int total = 0;
        for (int i = 0; i < selections.size(); i++) {
            long key = selections.keyAt(i);
            QuizChallengeQuestionState st = selections.get(key);
            if (st != null && st.completed) {
                total++;
            }
        }
        return total;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadState();
        updateProgressBar();
    }

    @Override
    public void onStop() {
        if (!challengeCompleted) {
            saveState();
        }
        if (entrySubscription != null) {
            entrySubscription.unsubscribe();
        }
        super.onStop();
    }

    public int calcPagerIdx(int idx) {
        if (idx >= mPager.getChildCount()) idx = mPager.getChildCount() - 1;
        if (idx < 0) idx = 0;
        return idx;
    }

    private void saveState() {
        persisted.saveQuizChallengeState(selections);
        persisted.saveQuizChallengeAnswers(answers);
    }

    private void loadState() {
        LongSparseArray<QuizChallengeQuestionState> state = null;
        List<ParticipantAnswer> prevAnswers = null;
        try {
            Log.d(TAG, "Loading state. (0/2)");
            state = persisted.loadQuizChallengeState();
            Log.d(TAG, "Loading answers. (1/2)");
            prevAnswers = persisted.loadQuizChallengeAnswers();
            Log.d(TAG, "Quiz state loaded. (2/2)");
        } catch (Exception e) {
            Log.d(TAG, "Failed to load quiz state. Resetting.");
            e.printStackTrace();
            clearState();
        }

        if (state != null) {
            selections = state;
        }

        if (prevAnswers != null) {
            answers = persisted.loadQuizChallengeAnswers();
        }

        int len = challenge.numQuestions();
        int idx = 0;
        List<QuizChallengeQuestion > questions = challenge.getQuestions();
        while (idx < len && isCompleted(questions.get(idx).getId())) {
            idx++;
        }
        mPager.setCurrentItem(idx);
    }

    private void clearState() {
        persisted.clearQuizChallengeState();
        persisted.clearQuizChallengeAnswers();
    }

    public QuizChallengeQuestionState getQuestionState(long id) {
        return selections.get(id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putSerializable(ChallengeActivity.ARG_PAGE, FRAGMENT_STATE);
            persisted.setParticipant(participant);
            outState.putParcelable(ChallengeActivity.ARG_CHALLENGE, challenge);
            outState.putParcelableArray(ARG_ANSWERS, answers.toArray(new ParticipantAnswer[]{}));
            Bundle state = new Bundle();
            for (int i = 0; i < selections.size(); i++) {
                state.putParcelable(String.valueOf(selections.keyAt(i)), selections.get(i));
            }
            outState.putBundle(ARG_STATE, state);
            View focusedTab = mPager.getFocusedChild();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            participant = persisted.getParticipant();
            challenge = savedInstanceState.getParcelable(ChallengeActivity.ARG_CHALLENGE);
            ParticipantAnswer storedAnswers[] = (ParticipantAnswer[]) savedInstanceState.getParcelableArray(ARG_ANSWERS);
            if (storedAnswers != null) {
                answers = Arrays.asList(storedAnswers);
            }
            Bundle storedSelections = savedInstanceState.getBundle(ARG_STATE);
            if (storedSelections != null) {
                for (String key: storedSelections.keySet()) {
                    selections.put(Long.valueOf(key), (QuizChallengeQuestionState) storedSelections.getParcelable(key));
                }
            }
        }
        super.onActivityCreated(savedInstanceState);
    }
}