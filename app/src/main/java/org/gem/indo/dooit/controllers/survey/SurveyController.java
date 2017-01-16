package org.gem.indo.dooit.controllers.survey;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.SurveyManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

abstract public class SurveyController extends DooitBotController {

    @Inject
    protected SurveyManager surveyManager;

    @Inject
    protected Persisted persisted;

    protected CoachSurvey survey;

    public SurveyController(Context context, BotType botType, CoachSurvey survey) {
        super(context, botType);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        this.survey = survey;
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {

    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    protected boolean hasSurvey() {
        return survey != null;
    }
}