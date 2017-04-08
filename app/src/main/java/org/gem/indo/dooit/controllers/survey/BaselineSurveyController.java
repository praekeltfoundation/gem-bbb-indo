package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Response;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

public class BaselineSurveyController extends SurveyController {

    private static final String BASELINE_CONSENT_1 = "survey_baseline_q1_consent";
    private static final String BASELINE_CONSENT_2 = "survey_baseline_q2_consent";

    private static final String[] QUESTIONS = new String[]{
            "survey_baseline_intro",

            BASELINE_CONSENT_1,
            BASELINE_CONSENT_2,

            "survey_baseline_q01_occupation",
            "survey_baseline_q02_grade",
            "survey_baseline_q03_school_name", // Inline text
            "survey_baseline_q04_city", // Inline text
            "survey_baseline_q05_job_month",
            "survey_baseline_q06_job_earning_range",
            "survey_baseline_q07_job_status",
            "survey_baseline_q08_shared_ownership",
            "survey_baseline_q09_business_earning_range",
            "survey_baseline_q10_save",
            "survey_baseline_q11_savings_frequency",
            "survey_baseline_q12_savings_where",
            "survey_baseline_q13_savings_3_months",
            "survey_baseline_q14_saving_education",
            "survey_baseline_q15_job_hunt",
            "survey_baseline_q16_emergencies",
            "survey_baseline_q17_invest",
            "survey_baseline_q18_family",
            "survey_baseline_q19_clothes_food",
            "survey_baseline_q21_gadgets",
            "survey_baseline_q22_friends",
            "survey_baseline_q23_mobile_frequency",
            "survey_baseline_q24_mobile_most_use",
            "survey_baseline_q25_mobile_least_use",
            "survey_baseline_q26_mobile_own",
            "survey_baseline_q27_mobile_use_opinion",
            "survey_baseline_q27_1_friends",
            "survey_baseline_q27_2_family",
            "survey_baseline_q27_3_community",
            "survey_baseline_q28_mobile_credit",
            "survey_baseline_q29_assets",
            "survey_baseline_q29_1_desktop",
            "survey_baseline_q29_2_laptop",
            "survey_baseline_q29_3_mobile_no_data",
            "survey_baseline_q29_4_mobile_data"
    };

    public BaselineSurveyController(Context context, CoachSurvey survey) {
        super(context, BotType.SURVEY_BASELINE, survey);
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

        Map<String, String> submission = new LinkedHashMap<>();

        // Consent
        handleConsent(submission, answerLog.get(BASELINE_CONSENT_1), answerLog.get(BASELINE_CONSENT_2));

        // Survey Questions
        for (String questionName : QUESTIONS) {
            Answer answer = answerLog.get(questionName);
            if (answer != null && answer.hasValue()
                    // Don't overwrite an existing answer submission
                    && !submission.containsKey(questionName))
                submission.put(questionName, answer.getValue());
            else
                submission.put(questionName, Integer.toString(ANSWER_NOT_APPLICABLE));
        }

        if (answerEquals(answerLog.get("survey_baseline_q05_job_month"), ANSWER_NO)
                || answerEquals(answerLog.get("survey_baseline_q05_job_month"), ANSWER_MISSING)) {
            // User skipped job branch
            submission.put("survey_baseline_q06_job_earning_range",
                    Integer.toString(ANSWER_NOT_APPLICABLE));
            submission.put("survey_baseline_q07_job_status",
                    Integer.toString(ANSWER_NOT_APPLICABLE));
        }

        if (answerEquals(answerLog.get("survey_baseline_q08_shared_ownership"), ANSWER_NO)
                || answerEquals(answerLog.get("survey_baseline_q08_shared_ownership"), ANSWER_MISSING))
            // User skipped the shared business branch
            submission.put("survey_baseline_q09_business_earning_range",
                    Integer.toString(ANSWER_NOT_APPLICABLE));

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
