package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.ChallengeAPI;
import org.gem.indo.dooit.api.responses.ChallengeAvailableReminderResponse;
import org.gem.indo.dooit.api.responses.ChallengeCompletionReminderResponse;
import org.gem.indo.dooit.api.responses.WinnerResponse;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.ParticipantFreeformAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallengeEntry;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
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

    public Observable<BaseChallenge> retrieveCurrentChallenge(boolean excludeDone, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.getCurrentChallenge(), errorHandler);
    }

    public Observable<QuizChallengeEntry> createEntry(QuizChallengeEntry quizChallengeEntry, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.postEntry(quizChallengeEntry), errorHandler);
    }

    public Observable<ParticipantFreeformAnswer> createParticipantFreeformAnswer(ParticipantFreeformAnswer answer, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.postParticipantFreeform(answer), errorHandler);
    }

    public Observable<ParticipantFreeformAnswer> fetchParticipantFreeformAnswer(long challengeID, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.fetchParticipantFreeform(challengeID), errorHandler);
    }

    public Observable<Participant> registerParticipant(Participant participant, DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.registerParticipant(participant), errorHandler);
    }

    public Observable<WinnerResponse> fetchChallengeWinner(DooitErrorHandler errorHandler){
        return useNetwork(challengeAPI.checkChallengeWinner(),errorHandler);
    }

    public Observable<Response<Void>> confirmChallengeWinnerNotification(Long id, DooitErrorHandler errorHandler){
        return useNetwork(challengeAPI.confirmChallengeWinnerNotification(id), errorHandler);
    }

    public Observable<ChallengeAvailableReminderResponse> getUserParticipatedWithinTwoDays(DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.getUserParticipatedWithinTwoDays(), errorHandler);
    }

    public Observable<ChallengeCompletionReminderResponse> hasUserNotSubmitted(DooitErrorHandler errorHandler) {
        return useNetwork(challengeAPI.getHasUserNotSubmitted(), errorHandler);
    }
}
