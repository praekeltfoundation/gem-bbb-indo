package com.nike.dooit.views.main.fragments.challenge.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nike.dooit.models.challenge.QuizChallenge;
import com.nike.dooit.models.challenge.QuizChallengeQuestion;
import com.nike.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;

import java.util.List;

/**
 * Created by wsche on 2016/11/05.
 */

public class ChallengeQuizPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private QuizChallenge challenge;
    private List<QuizChallengeQuestion> questions;

    public ChallengeQuizPagerAdapter(FragmentManager fm, Context context, QuizChallenge challenge) {
        super(fm);
        this.context = context;
        this.challenge = challenge;
        if (challenge != null) {
            this.questions = challenge.getQuestions();
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = ChallengeQuizQuestionFragment.newInstance(questions.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return questions != null ? questions.size() : 0;
    }
}
