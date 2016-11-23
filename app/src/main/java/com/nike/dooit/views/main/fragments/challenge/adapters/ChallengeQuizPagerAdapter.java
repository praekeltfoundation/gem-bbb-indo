package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseIntArray;

import com.nike.dooit.R;
import com.nike.dooit.models.challenge.QuizChallenge;
import com.nike.dooit.models.challenge.QuizChallengeOption;
import com.nike.dooit.models.challenge.QuizChallengeQuestion;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeCompleteFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionSelectedListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnQuestionCompletedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.OnPageChange;

/**
 * Created by wsche on 2016/11/05.
 */

public class ChallengeQuizPagerAdapter extends FragmentStatePagerAdapter implements OnOptionChangeListener, OnQuestionCompletedListener {
    private static final String TAG = "QuizPager";
    private QuizChallenge challenge;
    private List<QuizChallengeQuestion> questions;
    private ChallengeQuizFragment controller = null;

    public ChallengeQuizPagerAdapter(ChallengeQuizFragment challengeFragment, FragmentManager fm, QuizChallenge challenge) {
        super(fm);
        this.challenge = challenge;
        if (challenge != null) {
            this.questions = challenge.getQuestions();
        }
        if (challengeFragment != null) {
            controller = challengeFragment;
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (questions == null) {
            return null;
        }

        if (position == questions.size()) {
            return ChallengeCompleteFragment.newInstance(challenge);
        } else {
            ChallengeQuizQuestionFragment f;
            QuizChallengeQuestion question = questions.get(position);
            long optionId = controller.getSelectedOption(question.getId());
            boolean completed = controller.isCompleted(question.getId());
            f = ChallengeQuizQuestionFragment.newInstance(question, optionId, completed);
            f.setOnOptionChangeListener(this);
            return f;
        }
    }

    @Override
    public int getCount() {
        return questions != null ? questions.size() + 1 : 0;
    }

    @Override
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
    }

    @Override
    public void onQuestionCompleted(long id) {

    }
}
