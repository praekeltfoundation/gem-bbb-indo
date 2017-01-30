package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.SurveyResponse;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.List;
import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public interface SurveyAPI {

    @GET("/api/surveys/{id}/")
    Observable<CoachSurvey> getSurvey(@Path("id") long id, @Query("bot-conversation") BotType type);

    @GET("/api/surveys/")
    Observable<List<CoachSurvey>> getSurveys(@Query("bot-conversation") BotType type);

    @GET("/api/surveys/current/")
    Observable<SurveyResponse> getCurrentSurvey();

    @POST("/api/surveys/{id}/submission/")
    Observable<Response<Void>> createSubmission(@Path("id") long id, @Body Map<String, String> submission);

    @PATCH("/api/surveys/{id}/draft/")
    Observable<Response<Void>> updateDraft(@Path("id") long id, @Body Map<String, String> submission);
}
