package com.nike.dooit.views.main.fragments.challenge.interfaces;

import com.nike.dooit.models.challenge.QuizChallengeOption;
import com.nike.dooit.models.challenge.QuizChallengeQuestion;

/**
 * Created by Rudolph Jacobs on 2016-11-12.
 */

public interface OnOptionChangeListener {
    public void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option);
}