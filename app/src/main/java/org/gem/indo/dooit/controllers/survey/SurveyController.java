package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

abstract public class SurveyController extends DooitBotController {

    protected static final String YES = "yes";
    protected static final String NO = "no";

    public SurveyController(Context context, BotType botType) {
        super(context, botType);
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
}
