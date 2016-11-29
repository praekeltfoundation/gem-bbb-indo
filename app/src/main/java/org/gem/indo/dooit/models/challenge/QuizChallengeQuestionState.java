package org.gem.indo.dooit.models.challenge;

/**
 * Created by Rudolph Jacobs on 2016-11-29.
 */

public class QuizChallengeQuestionState {
    public long optionId = -1;
    public boolean completed = false;

    public QuizChallengeQuestionState(long optionId, boolean completed) {
        this.optionId = optionId;
        this.completed = completed;
    }
}
