package org.gem.indo.dooit.api.managers;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.SurveyAPI;
import org.gem.indo.dooit.api.responses.SurveyResponse;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.List;
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

    public Observable<CoachSurvey> retrieveSurvey(long id, @Nullable BotType botType,
                                                  DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.getSurvey(id, botType), errorHandler);
    }

    public Observable<List<CoachSurvey>> retrieveSurveys(@Nullable BotType botType,
                                                         DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.getSurveys(botType), errorHandler);
    }

    public Observable<SurveyResponse> retrieveCurrentSurvey(DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.getCurrentSurvey(), errorHandler);
    }

    public Observable<Response<Void>> submit(long surveyId, @NonNull Map<String, String> submission,
                                             DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.createSubmission(surveyId, submission), errorHandler);
    }

    public Observable<Response<Void>> draft(long surveyId, @NonNull Map<String, String> submission,
                                            DooitErrorHandler errorHandler) {
        return useNetwork(surveyAPI.updateDraft(surveyId, submission), errorHandler);
    }
}
