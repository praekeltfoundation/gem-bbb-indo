package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.SurveyResponse;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public interface SurveyAPI {

    @GET("/api/surveys/current/")
    Observable<SurveyResponse> getCurrentSurvey();

    @POST("/api/surveys/{id}/submission/")
    Observable<Response<Void>> createSubmission(@Path("id") long id, @Body Map<String, String> submission);
}
