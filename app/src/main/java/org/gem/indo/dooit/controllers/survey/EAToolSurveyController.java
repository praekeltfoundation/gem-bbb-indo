package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/01/19.
 */

public class EAToolSurveyController extends SurveyController {

    private static final String EATOOL_CONSENT = "survey_eatool_q_consent";
    private static final String[] QUESTIONS = new String[]{
            "survey_eatool_q01_appreciate",
            "survey_eatool_q02_optimistic",
            "survey_eatool_q03_career",
            "survey_eatool_q04_adapt",
            "survey_eatool_q05_duties",
            "survey_eatool_q06_lazy",
            "survey_eatool_q07_proud",
            "survey_eatool_q08_dress",
            "survey_eatool_q09_along",
            "survey_eatool_q10_anyone",
            "survey_eatool_q11_input",
            "survey_eatool_q12_responsible",
            "survey_eatool_q13_express",
            "survey_eatool_q14_understand",
            "survey_eatool_q15_read",
            "survey_eatool_q16_learn",
            "survey_eatool_q17_organise",
            "survey_eatool_q18_sources",
            "survey_eatool_q19_experience",
            "survey_eatool_q20_adapting",
            "survey_eatool_q21_interview",
            "survey_eatool_q22_cv",
            "survey_eatool_q23_cover",
            "survey_eatool_q24_company"
    };

    public EAToolSurveyController(Context context, CoachSurvey survey) {
        super(context, BotType.SURVEY_EATOOL, survey);
    }

    @Override
    protected String[] getQuestionKeys() {
        return QUESTIONS;
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case DO_CREATE:
                submitSurvey(answerLog, listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
        }
    }

    private void submitSurvey(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        if (!hasSurvey()) {
            notifyDone(listener);
            return;
        }

        Map<String, String> submission = new HashMap<>();

        // Consent
        handleConsent(submission, answerLog.get(EATOOL_CONSENT));

        // Survey Questions
        for (String questionName : QUESTIONS) {
            Answer answer = answerLog.get(questionName);
            if (answer != null && answer.hasValue()
                    // Don't overwrite an existing answer submission
                    && !submission.containsKey(questionName))
                submission.put(questionName, answer.getValue());
            else
                submission.put(questionName, Integer.toString(ANSWER_MISSING));
        }

        if (hasSurvey())
            surveyManager.submit(survey.getId(), submission, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }).doAfterTerminate(new Action0() {
                @Override
                public void call() {
                    notifyDone(listener);
                }
            }).subscribe(new Action1<Response<Void>>() {
                @Override
                public void call(Response<Void> voidResponse) {
                    // TODO: Remove push notification if it's still in the phone's notification drawer
                    // After user has successfully submitted, they should not be able to take the
                    // survey again. The server endpoint should prevent further notifications.
                    persisted.clearConvoSurvey(botType);
                }
            });
    }
}
