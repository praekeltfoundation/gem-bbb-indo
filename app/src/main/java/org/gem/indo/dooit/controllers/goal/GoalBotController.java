package org.gem.indo.dooit.controllers.goal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.bot.param.ParamArg;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class GoalBotController extends DooitBotController {

    @Inject
    protected GoalManager goalManager;

    protected BotRunner botRunner;
    protected List<GoalPrototype> prototypes = new ArrayList<>();
    protected Goal goal;
    protected BaseChallenge challenge;
    // The Tip to be shown at the end of the conversation
    protected Tip tip;

    private Handler handler = new Handler(Looper.getMainLooper());

    public GoalBotController(Context context, BotRunner botRunner, BotType botType, Goal goal,
                             BaseChallenge challenge, Tip tip) {
        super(context, botType);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        this.botRunner = botRunner;
        this.goal = goal;
        this.challenge = challenge;
        this.tip = tip;
    }

    public GoalBotController(Context context, BotRunner botRunner, BotType botType,
                             List<GoalPrototype> prototypes, Goal goal, BaseChallenge challenge, Tip tip) {
        this(context, botRunner, botType, goal, challenge, tip);
        if (prototypes != null)
            this.prototypes.addAll(prototypes);
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case ADD_BADGE:
                doAddBadge();
                break;
        }
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();

        switch (paramType) {
            case GOAL_NAME:
                if (goal.hasName())
                    model.values.put(key, goal.getName());
                else if (goal.hasPrototype())
                    model.values.put(key, goal.getPrototype().getName());
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
                model.values.put(key, (int) goal.getWeeksLeft(WeekCalc.Rounding.UP));
                break;
            case GOAL_WEEKS_LEFT_DOWN:
                model.values.put(key, (int) goal.getWeeksLeft(WeekCalc.Rounding.DOWN));
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
            case CURRENCY_SYMBOL:
                model.values.put(key, CurrencyHelper.getCurrencySymbol());
                break;
            case GOAL_PROTO_NUM_USERS_WITH_SIMILAR_GOALS:
                if (goal.hasPrototype())
                    model.values.put(key, goal.getPrototype().getNumUsers());
                break;
            case GOAL_PROTO_DEFAULT_PRICE:
                if(goal.hasPrototype())
                    model.values.put(key, goal.getPrototype().getDefaultPrice());
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        switch (inputType) {
            case GOAL_PROTO:
                goal.setPrototype(new GoalPrototype(
                        answer.values.getLong(BotParamType.GOAL_PROTO_ID.getKey()),
                        answer.values.getString(BotParamType.GOAL_PROTO_NAME.getKey()),
                        answer.values.getString(BotParamType.GOAL_PROTO_IMAGE_URL.getKey()),
                        answer.values.getInteger(BotParamType.GOAL_PROTO_NUM_USERS_WITH_SIMILAR_GOALS.getKey()),
                        answer.values.getDouble(BotParamType.GOAL_PROTO_DEFAULT_PRICE.getKey()))
                );
                break;
            case GOAL_NAME:
                goal.setName(answer.getValue());
                break;
            case GOAL_TARGET:
                goal.setTarget(Double.parseDouble(answer.getValue()));
                break;
            case GOAL_VALUE:
                goal.setValue(Double.parseDouble(answer.getValue()));
                break;
            case GOAL_WEEKLY_TARGET: {
                LocalDate startDate = goal.hasStartDate() ? goal.getStartDate() : LocalDate.now();
                goal.setEndDate(new LocalDate(Goal.endDateFromTarget(
                        startDate.toDate(), goal.getTarget(), Double.parseDouble(answer.getValue()))));
                break;
            }
            default:
                super.onAnswerInput(inputType, answer);
        }
    }

    @Override
    public boolean shouldSkip(BaseBotModel model) {
        if (model.getCall() == BotCallType.ADD_BADGE && !goal.hasNewBadges())
            return true;
        else if (model.getMessageType() == BotMessageType.GOALGALLERY && prototypes.isEmpty())
            return true;
        else
            return super.shouldSkip(model);
    }

    @Override
    public Object getObject() {
        return goal;
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case GOAL_PROTOTYPES:
                return prototypes;
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

    private void doAddBadge() {
        for (Badge badge : goal.getNewBadges())
            botRunner.addNode(nodeFromBadge(badge));
    }

    private Node nodeFromBadge(Badge badge) {
        // TODO: Think of a unified way to construct Nodes programmatically. Should it be done in the view holders? Factories?

        String badgeName = botType.name().toLowerCase() + "_" + badge.getGraphName();

        // Badge Graphic Display
        Node node = new Node();
        node.setName(badgeName);
        node.setType(BotMessageType.BADGE);
        node.setText(null);

        node.values.put(BotParamType.BADGE_NAME.getKey(), badge.getName());
        node.values.put(BotParamType.BADGE_IMAGE_URL.getKey(), badge.getImageUrl());
        node.values.put(BotParamType.BADGE_SOCIAL_URL.getKey(), badge.getSocialUrl());
        node.finish();

        if (badge.hasIntro()) {
            // Badge Intro Text
            Node introNode = new Node();
            introNode.setName(badgeName + "_intro");
            introNode.setType(BotMessageType.TEXT);
            introNode.setAutoNext(node);

            // TODO: Refactor Param parsing and populating into DooitBotController
            // TODO: Text is processed here because Nodes currently don't support having text sourced from somewhere that's not the strings.xml files
            ParamMatch args = ParamParser.parse(badge.getIntro());
            if (!args.isEmpty())
                for (ParamArg arg : args.getArgs())
                    resolveParam(introNode, BotParamType.byKey(arg.getKey()));
            introNode.setProcessedText(args.process(introNode.values.getRawMap()));

            return introNode;
        } else
            return node;
    }

    protected void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
}
