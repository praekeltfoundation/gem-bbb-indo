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
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionSelectedListener;
import com.nike.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnPageChange;

/**
 * Created by wsche on 2016/11/05.
 */

public class ChallengeQuizPagerAdapter extends FragmentStatePagerAdapter implements OnOptionChangeListener {
    private static final String TAG = "QuizPager";
    private QuizChallenge challenge;
    private List<QuizChallengeQuestion> questions;
    private Map<Long, Long> selections = new HashMap<>();
    private OnOptionChangeListener optionChangeListener = null;

    public ChallengeQuizPagerAdapter(FragmentManager fm, QuizChallenge challenge) {
        super(fm);
        this.challenge = challenge;
        if (challenge != null) {
            this.questions = challenge.getQuestions();
        }
    }

    public void setOnOptionChangeListener(OnOptionChangeListener listener) {
        this.optionChangeListener = listener;
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
            long optionId = -1;
            if (question != null && selections.containsKey(question.getId())) {
                optionId = selections.get(question.getId());
            }
            f = ChallengeQuizQuestionFragment.newInstance(question, optionId);
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
        selections.put(question.getId(), option.getId());
        if (optionChangeListener != null) {
            optionChangeListener.onOptionChange(question, option);
        }
    }
}
