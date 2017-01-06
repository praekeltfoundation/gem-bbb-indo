package org.gem.indo.dooit.controllers.misc;

import android.content.Context;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;

import java.util.List;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/09.
 */

public class ReturningUserController extends DooitBotController {

    private BotRunner botRunner;
    private BaseChallenge challenge;
    private List<Goal> goals;
    private Tip tip;

    public ReturningUserController(Context context, BotRunner botRunner,
                                   List<Goal> goals, BaseChallenge challenge, Tip tip) {
        super(context, BotType.RETURNING_USER);
        this.botRunner = botRunner;
        this.goals = goals;
        this.challenge = challenge;
        this.tip = tip;
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void call(BaseBotModel model, String key) {

    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();
        switch (paramType) {
            case CHALLENGE_INTRO:
                model.values.put(key, challenge.getIntro());
                break;
            case CHALLENGE_OUTRO:
                model.values.put(key, challenge.getOutro());
                break;
            case TIP_INTRO:
                model.values.put(key, tip.getIntro());
                break;
            default:
                super.resolveParam(model, paramType);
                break;
        }
    }

    @Override
    public boolean filter(Answer answer) {
        switch (answer.getName()) {
            case "convo_default_return_a_progress_intro_tip":
                return tip != null;
            case "convo_default_return_a_progress_intro_challenge":
                return challenge != null && challenge.isActive();
            default:
                return true;
        }
    }

    @Override
    public boolean shouldSkip(BaseBotModel model) {
        if (model.getName().equals("convo_default_return_q_summary"))
            return goals.isEmpty();
        else
            return super.shouldSkip(model);
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case CHALLENGE:
                return challenge;
            case TIP:
                return tip;
            case GOALS:
                return goals;
            default:
                return super.getObject(objType);
        }
    }
}
