package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.requests.AddCaptionRequest;
import org.gem.indo.dooit.api.responses.ChallengeAvailableReminderResponse;
import org.gem.indo.dooit.api.responses.ChallengeCompletionReminderResponse;
import org.gem.indo.dooit.api.responses.ChallengeParticipatedResponse;
import org.gem.indo.dooit.api.responses.WinnerResponse;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.Participant;
import org.gem.indo.dooit.models.challenge.ParticipantFreeformAnswer;
import org.gem.indo.dooit.models.challenge.QuizChallengeEntry;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface ChallengeAPI {

    @GET("/api/challenges/")
    Observable<List<BaseChallenge>> getChallenges();

    @GET("/api/challenges/{id}/")
    Observable<BaseChallenge> getChallenge(@Path("id") int id);

    @GET("/api/challenges/current/")
    Observable<BaseChallenge> getCurrentChallenge();

    @POST("/api/entries/")
    Observable<QuizChallengeEntry> postEntry(@Body QuizChallengeEntry quizChallengeEntry);

    @POST("/api/participantfreetext/")
    Observable<ParticipantFreeformAnswer> postParticipantFreeform(@Body ParticipantFreeformAnswer answer);

    @GET("/api/participantfreetext/fetch/")
    Observable<ParticipantFreeformAnswer> fetchParticipantFreeform(@Query("challenge") long challenge);

    @POST("/api/participants/register/")
    Observable<Participant> registerParticipant(@Body Participant participant);

    @GET("/api/participants/check_participation/")
    Observable<ChallengeParticipatedResponse> checkChallengeParticipation();

    @GET("/api/challenges/winning/")
    Observable<WinnerResponse> checkChallengeWinner();

    @POST("/api/challenges/{id}/notification/")
    Observable<Response<Void>> confirmChallengeWinnerNotification(@Path("id") Long id);

    @POST("/api/participantpicture/{id}/caption/")
    Observable<Response<Void>> addPictureChallengeCaption(@Path("id") Long id, @Body AddCaptionRequest request);

    @GET("/api/challenges/participation")
    Observable<ChallengeAvailableReminderResponse> getUserParticipatedWithinTwoDays();

    @GET("/api/challenges/challenge_incomplete")
    Observable<ChallengeCompletionReminderResponse> getHasUserNotSubmitted();
}
