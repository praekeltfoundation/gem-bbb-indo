package com.nike.dooit.api.managers;

import android.app.Application;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.interfaces.ChallengeAPI;
import com.nike.dooit.models.challenge.ParticipantFreeformAnswer;
import com.nike.dooit.models.challenge.QuizChallengeEntry;
import com.nike.dooit.models.challenge.BaseChallenge;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public class ChallengeManager extends DooitManager {

    private final ChallengeAPI challengeAPI;

    @Inject
    public ChallengeManager(Application application) {
        super(application);
        challengeAPI = retrofit.create(ChallengeAPI.class);
    }

    public Observable<List<BaseChallenge>> retrieveChallenges(DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.getChallenges(), errorHandler);
    }

    public Observable<BaseChallenge> retrieveChallenge(int challengeId, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.getChallenge(challengeId), errorHandler);
    }

    public Observable<QuizChallengeEntry> createEntry(QuizChallengeEntry quizChallengeEntry, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.postEntry(quizChallengeEntry), errorHandler);
    }

    public Observable<ParticipantFreeformAnswer> createParticipantFreeformAnswer(ParticipantFreeformAnswer answer, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.postParticipantFreeform(answer), errorHandler);
    }
}
