package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

public class BaselineSurveyController extends SurveyController {

    private static final String[] QUESTIONS = new String[]{
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
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        if (!hasSurvey()) {
            super.resolveParam(model, paramType);
            return;
        }

        String key = paramType.getKey();
        switch (paramType) {
            case SURVEY_TITLE:
                model.values.put(key, survey.getTitle());
                break;
            case SURVEY_INTRO:
                model.values.put(key, survey.getIntro());
                break;
            case SURVEY_OUTRO:
                model.values.put(key, survey.getOutro());
                break;
            default:
                super.resolveParam(model, paramType);
        }
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
        if (!hasSurvey())
            return;

        Map<String, String> submission = new HashMap<>();
        for (String questionName : QUESTIONS) {
            Answer answer = answerLog.get(questionName);
            if (answer != null && answer.hasValue()) {
                submission.put(questionName, answer.getValue());

                // Handle cases where the user jumped ahead in the survey
                switch (answer.getName()) {
                    case "survey_baseline_q05_job_month":
                        if (answerEquals(answer, ANSWER_NO)) {
                            // User skipped job branch
                            submission.put("survey_baseline_q06_job_earning_range",
                                    Integer.toString(ANSWER_NOT_APPLICABLE));
                            submission.put("survey_baseline_q07_job_status",
                                    Integer.toString(ANSWER_NOT_APPLICABLE));
                        }
                        break;
                    case "survey_baseline_q08_shared_ownership":
                        if (answerEquals(answer, ANSWER_NO))
                            // User skipped the shared business branch
                            submission.put("survey_baseline_q09_business_earning_range",
                                    Integer.toString(ANSWER_NOT_APPLICABLE));
                        break;
                }
            }
        }

        surveyManager.submit(10, submission, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
            }
        }).subscribe();
    }
}
