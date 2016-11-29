package org.gem.indo.dooit.models.challenge;

import java.io.Serializable;

/**
 * Created by Rudolph Jacobs on 2016-11-29.
 */

public class QuizChallengeQuestionState implements Serializable {
    public long optionId = -1;
    public boolean completed = false;

    public QuizChallengeQuestionState(long optionId, boolean completed) {
        this.optionId = optionId;
        this.completed = completed;
    }
}
