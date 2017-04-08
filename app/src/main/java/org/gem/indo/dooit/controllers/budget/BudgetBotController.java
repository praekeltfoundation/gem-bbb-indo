package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.bot.param.ParamArg;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2017/03/30.
 */

public abstract class BudgetBotController extends DooitBotController {

    protected static String INCOME_INPUT = "income_amount";
    protected static String SAVINGS_INPUT = "savings_amount";

    protected BotRunner botRunner;
    protected MainActivity activity;
    protected Budget budget;

    public BudgetBotController(Context context, BotType botType,
                               @NonNull Activity activity, @NonNull BotRunner botRunner) {
        super(context, botType);

        this.activity = (MainActivity) activity;
        this.botRunner = botRunner;
    }

    @Override
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case BUDGET:
                return budget;
            default:
                return super.getObject(objType);
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case SET_TIP_QUERY:
                tipQuery();
                break;
            case SET_TIP_QUERY_BUDGET:
                tipQueryBudget();
                break;
            case ADD_BADGE:
                doAddBadge();
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    private void tipQuery() {
        if (activity == null)
            return;
        activity.setTipQuery(activity.getString(R.string.budget_create_qry_tip_income));
    }

    private void tipQueryBudget(){
        if (activity == null)
            return;
        activity.setTipQuery(activity.getString(R.string.budget_create_qry_tip_budget));
    }

    ////////////////
    // Validation //
    ////////////////


    @Override
    public boolean validate(String name, String input) {
        switch (name) {
            case "income_amount":
                return validateIncome(input);
            case "savings_amount":
                return validateSavings(input);
            default:
                return super.validate(name, input);
        }
    }

    boolean validateIncome(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_income__empty);
            return false;
        }
        try {
            double income = Double.parseDouble(input);
            if (income <= 0.0) {
                toast(R.string.budget_create_err_income__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast("Invalid number");
            return false;
        }
    }

    boolean validateSavings(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_savings__empty);
            return false;
        }
        try {
            double value = Double.parseDouble(input);

            // Find income defined earlier
            Map<String, Answer> answerLog = botRunner.getAnswerLog();
            Double income = null;
            if (answerLog.containsKey(INCOME_INPUT))
                income = Double.parseDouble(answerLog.get(INCOME_INPUT).getValue());

            if (value <= 0.0) {
                toast(R.string.budget_create_err_savings__zero);
                return false;
            } else if (income != null && value > income) {
                toast(R.string.budget_create_err_savings__gt_income);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.budget_create_err__invalid_number);
            return false;
        }
    }

    boolean validateExpense(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_expense__empty);
            return false;
        }
        try {
            double expense = Double.parseDouble(input);

            if (expense <= 0) {
                toast(R.string.budget_create_err_expense__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.budget_create_err__invalid_number);
            return false;
        }
    }

    private void doAddBadge() {
        List<Badge> badges = persisted.loadNewBudgetBadges();
        if(badges.size() > 0) {
            for (Badge badge : badges)
                botRunner.queueNode(nodeFromBadge(badge));
        }else{
            Node node = new Node();
            node.setName("save_Conversation_Node");
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext("budget_create_q_final_positive_has_goals");
            botRunner.queueNode(node);
        }
    }

    private Node nodeFromBadge(Badge badge) {
        // TODO: Think of a unified way to construct Nodes programmatically. Should it be done in the view holders? Factories?

        String badgeName = botType.name().toLowerCase() + "_" + badge.getGraphName();

        // Badge Graphic Display
        Node node = new Node();
        node.setName(badgeName);
        node.setType(BotMessageType.BADGE);
        node.setText(null);
        node.setAutoNext("budget_create_a_yay");

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
}
