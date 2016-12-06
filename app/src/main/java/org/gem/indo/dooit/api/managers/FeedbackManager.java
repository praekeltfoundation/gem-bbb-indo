package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.ChallengeAPI;
import org.gem.indo.dooit.api.interfaces.FeedbackAPI;
import org.gem.indo.dooit.models.UserFeedback;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.ParticipantFreeformAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallengeEntry;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public class FeedbackManager extends DooitManager {

    private final FeedbackAPI feedbackAPI;

    @Inject
    public FeedbackManager(Application application) {
        super(application);
        feedbackAPI = retrofit.create(FeedbackAPI.class);
    }

    public Observable<UserFeedback> sendFeedback(UserFeedback feedback, DooitErrorHandler errorHandler) {
        return useNetwork(feedbackAPI.sendFeedback(feedback), errorHandler);
    }
}
