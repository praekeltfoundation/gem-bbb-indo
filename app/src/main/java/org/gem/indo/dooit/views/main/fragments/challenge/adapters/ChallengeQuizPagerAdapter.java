package org.gem.indo.dooit.views.main.fragments.challenge.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.gem.indo.dooit.models.challenge.QuizChallenge;
import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizQuestionFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.OnOptionChangeListener;

import java.util.List;

/**
 * Created by wsche on 2016/11/05.
 */

public class ChallengeQuizPagerAdapter extends FragmentStatePagerAdapter implements OnOptionChangeListener {
    private Context context;
    private QuizChallenge challenge;
    private List<QuizChallengeQuestion> questions;
    private OnOptionChangeListener optionChangeListener = null;

    public ChallengeQuizPagerAdapter(FragmentManager fm, Context context, QuizChallenge challenge) {
        super(fm);
        this.context = context;
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
        ChallengeQuizQuestionFragment f = ChallengeQuizQuestionFragment.newInstance(questions.get(position));
        f.setOnOptionChangeListener(this);
        return f;
    }

    @Override
    public int getCount() {
        return questions != null ? questions.size() : 0;
    }

    @Override
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option) {
        if (optionChangeListener != null) {
            optionChangeListener.onOptionChange(question, option);
        }
    }
}
