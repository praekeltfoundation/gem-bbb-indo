package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

public class BaselineSurveyController extends SurveyController {

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

    private void submitSurvey(Map<String, Answer> answerLog, OnAsyncListener listener) {
        listener.onDone();
    }
}
