package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.SurveyAPI;
import org.gem.indo.dooit.api.responses.SurveyResponse;

import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class SurveyManager extends DooitManager {

    private final SurveyAPI surveyAPI;

    @Inject
    public SurveyManager(Application application) {
        super(application);
        surveyAPI = retrofit.create(SurveyAPI.class);
    }

    public Observable<SurveyResponse> retrieveCurrentSurvey(DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.getCurrentSurvey(), errorHandler);
    }

    public Observable<Response<Void>> submit(long surveyId, Map<String, String> submission,
                                       DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.createSubmission(surveyId, submission), errorHandler);
    }
}
