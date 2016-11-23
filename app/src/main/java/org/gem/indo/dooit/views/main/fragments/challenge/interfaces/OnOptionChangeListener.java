package org.gem.indo.dooit.views.main.fragments.challenge.interfaces;

import org.gem.indo.dooit.models.challenge.QuizChallengeOption;
import org.gem.indo.dooit.models.challenge.QuizChallengeQuestion;

/**
 * Created by Rudolph Jacobs on 2016-11-12.
 */

public interface OnOptionChangeListener {
    void onOptionChange(QuizChallengeQuestion question, QuizChallengeOption option);
}
