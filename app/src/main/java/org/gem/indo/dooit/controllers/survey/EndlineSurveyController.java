package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.controllers.BotController;
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
 * Created by Chad Garrett on 2017/09/04.
 */

public class EndlineSurveyController extends SurveyController {

    private static final String ENDLINE_CONSENT_1 = "survey_endline_q1_consent";
    private static final String ENDLINE_CONSENT_2 = "survey_endline_q2_consent";

    private static final String[] QUESTIONS = new String[]{
            "survey_endline_intro",

            ENDLINE_CONSENT_1,
            ENDLINE_CONSENT_2,

            "survey_endline_q_user_type",
            "survey_endline_q01_occupation",
            "survey_endline_q02_grade",
            "survey_endline_q03_school_name", // Inline text
            "survey_endline_q04_city", // Inline text
            "survey_endline_q05_job_month",
            "survey_endline_q06_job_earning_range",
            "survey_endline_q07_job_status",
            "survey_endline_q08_shared_ownership",
            "survey_endline_q09_business_earning_range",
            "survey_endline_q10_save",
            "survey_endline_q11_savings_frequency",
            "survey_endline_q12_savings_where",
            "survey_endline_q13_savings_3_months",
            "survey_endline_q14_saving_education",
            "survey_endline_q15_job_hunt",
            "survey_endline_q16_emergencies",
            "survey_endline_q17_invest",
            "survey_endline_q18_family",
            "survey_endline_q19_clothes_food",
            "survey_endline_q21_gadgets",
            "survey_endline_q22_friends",
            "survey_endline_q23_mobile_frequency",
            "survey_endline_q24_mobile_most_use",
            "survey_endline_q25_mobile_least_use",
            "survey_endline_q26_mobile_own",
            "survey_endline_q27_mobile_use_opinion",
            "survey_endline_q27_1_friends",
            "survey_endline_q27_2_family",
            "survey_endline_q27_3_community",
            "survey_endline_q28_mobile_credit",
            "survey_endline_q29_assets",
            "survey_endline_q29_1_desktop",
            "survey_endline_q29_2_laptop",
            "survey_endline_q29_3_mobile_no_data",
            "survey_endline_q29_4_mobile_data"
    };

    public EndlineSurveyController(Context context, CoachSurvey survey) {
        super(context, BotType.SURVEY_ENDLINE, survey);
    }

    @Override
    protected String[] getQuestionKeys() {
        return QUESTIONS;
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, BotController.OnAsyncListener listener) {
        switch (key) {
            case DO_CREATE:
                submitSurvey(answerLog, listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
        }
    }

    private void submitSurvey(Map<String, Answer> answerLog, final BotController.OnAsyncListener listener) {
        if (!hasSurvey()) {
            notifyDone(listener);
            return;
        }

        Map<String, String> submission = new LinkedHashMap<>();

        // Consent
        handleConsent(submission, answerLog.get(ENDLINE_CONSENT_1), answerLog.get(ENDLINE_CONSENT_2));

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

        if (answerEquals(answerLog.get("survey_endline_q05_job_month"), ANSWER_NO)
                || answerEquals(answerLog.get("survey_endline_q05_job_month"), ANSWER_MISSING)) {
            // User skipped job branch
            submission.put("survey_endline_q06_job_earning_range",
                    Integer.toString(ANSWER_NOT_APPLICABLE));
            submission.put("survey_endline_q07_job_status",
                    Integer.toString(ANSWER_NOT_APPLICABLE));
        }

        if (answerEquals(answerLog.get("survey_endline_q08_shared_ownership"), ANSWER_NO)
                || answerEquals(answerLog.get("survey_endline_q08_shared_ownership"), ANSWER_MISSING))
            // User skipped the shared business branch
            submission.put("survey_endline_q09_business_earning_range",
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
