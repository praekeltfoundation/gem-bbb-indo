package org.gem.indo.dooit.controllers.goal;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class GoalBotController extends DooitBotController {

    @Inject
    protected GoalManager goalManager;

    protected Goal goal;
    protected BaseChallenge challenge;
    // The Tip to be shown at the end of the conversation
    protected Tip tip;

    public GoalBotController(Context context, BotType botType, Goal goal, BaseChallenge challenge, Tip tip) {
        super(context, botType);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        this.goal = goal;
        this.challenge = challenge;
        this.tip = tip;
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();

        switch (paramType) {
            case GOAL_NAME:
                model.values.put(key, goal.getName());
                break;
            case GOAL_VALUE:
                model.values.put(key, goal.getValue());
                break;
            case GOAL_TARGET:
                model.values.put(key, goal.getTarget());
                break;
            case GOAL_TARGET_CURRENCY:
                model.values.put(key, CurrencyHelper.format(goal.getTarget()));
                break;
            case GOAL_END_DATE:
                model.values.put(key, Utils.formatDate(goal.getEndDate().toDate()));
                break;
            case GOAL_WEEKLY_TARGET:
                model.values.put(key, goal.getWeeklyTarget());
                break;
            case GOAL_WEEKLY_TARGET_CURRENCY:
                model.values.put(key, CurrencyHelper.format(goal.getWeeklyTarget()));
                break;
            case GOAL_WEEKS_LEFT_UP:
                model.values.put(key, goal.getWeeksLeft(Utils.ROUNDWEEK.UP));
                break;
            case GOAL_WEEKS_LEFT_DOWN:
                model.values.put(key, goal.getWeeksLeft(Utils.ROUNDWEEK.DOWN));
                break;
            case GOAL_REMAINDER_DAYS_LEFT:
                model.values.put(key, goal.getRemainderDaysLeft());
                break;
            case GOAL_IMAGE_URL:
                model.values.put(key, goal.getImageUrl());
                break;
            case GOAL_LOCAL_IMAGE_URI:
                model.values.put(key, goal.getLocalImageUri());
                break;
            case GOAL_HAS_LOCAL_IMAGE_URI:
                model.values.put(key, goal.hasLocalImageUri());
                break;
            case CHALLENGE_INTRO:
                model.values.put(key, challenge.getIntro());
                break;
            case TIP_INTRO:
                model.values.put(key, tip.getIntro());
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }

    @Override
    public void input(BotParamType inputType, Object value) {
        // TODO: Currently unused
        switch (inputType) {
            case GOAL_NAME:
                goal.setName((String) value);
                break;
            case GOAL_TARGET:
                goal.setTarget((Double) value);
                break;
        }
    }

    @Override
    public Object getObject() {
        return goal;
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case GOAL:
                return goal;
            case CHALLENGE:
                return challenge;
            case TIP:
                return tip;
            default:
                return null;
        }
    }

    protected Node nodeFromBadge(Badge badge) {
        // TODO: Think of a unified way to construct Nodes programmatically. Should it be done in the view holders?
        Node node = new Node();
        node.setName(botType.name().toLowerCase() + badge.getGraphName());
        node.setType(BotMessageType.BADGE);
        node.setText(null);

        node.values.put(BotParamType.BADGE_NAME.getKey(), badge.getName());
        node.values.put(BotParamType.BADGE_IMAGE_URL.getKey(), badge.getImageUrl());
        node.values.put(BotParamType.BADGE_SOCIAL_URL.getKey(), badge.getSocialUrl());

        return node;
    }
}
