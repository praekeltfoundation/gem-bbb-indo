package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.SurveyResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public interface SurveyAPI {

    @GET("/api/surveys/current")
    Observable<SurveyResponse> getCurrentSurvey();
}
