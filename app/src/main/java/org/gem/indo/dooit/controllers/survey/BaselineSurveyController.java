package org.gem.indo.dooit.controllers.survey;

import android.content.Context;
import android.text.TextUtils;

import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.SurveyManager;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action0;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

public class BaselineSurveyController extends SurveyController {

    private static final String[] QUESTIONS = new String[]{
            "survey_baseline_q01_occupation",
            "survey_baseline_q02_grade",
            "survey_baseline_a_school_name", // Inline text
            "survey_baseline_a_city", // Inline text
            "survey_baseline_q05_job_month",
            "survey_baseline_q06_job_earning_range"
    };

    public BaselineSurveyController(Context context) {
        super(context, BotType.SURVEY_BASELINE);
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
        Map<String, String> submission = new HashMap<>();
        for (String questionName : QUESTIONS) {
            Answer answer = answerLog.get(questionName);
            if (answer != null && answer.hasValue())
                submission.put(questionName, answer.getValue());
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
