package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.bot.param.ParamArg;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
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
        } else {
            return node;
        }
    }

    /////////////////////////////
    // Expense input iteration //
    /////////////////////////////

    /**
     * @param questionPrefix The prefix of the question node. Will be suffixed by the category id.
     * @param answerPrefix   The prefix of the currency answer node. Will be suffixed by the
     *                       category id.
     * @param sourceNodeName The name of the node to loop back to when iteration continues.
     * @param stopNodeName   The name of the node to be inserted into the conversation when the
     *                       iteration stops.
     * @param endNodeName    The name of the node to go to when iteration is done.
     */
    protected void addNextExpense(@NonNull String questionPrefix, @NonNull String answerPrefix,
                                  @NonNull String sourceNodeName, @NonNull String stopNodeName,
                                  @NonNull String endNodeName) {

        ExpenseCategory category = ExpenseCategoryBotDAO.findNext(botType);

        // Stop Expense loop
        if (category == null) {
            // Special Node to stop the Expense loop and continue with the conversation
            Node node = new Node();
            node.setName(stopNodeName);
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext(endNodeName);

            botRunner.queueNode(node);
            return;
        }

        Context context = getContext();
        if (context == null)
            return;

        Node node = new Node();
        node.setName(questionPrefix + Long.toString(category.getId()));
        node.setType(BotMessageType.TEXT);

        ParamMatch match = ParamParser.parse(context.getString(R.string.budget_create_q_expense_value));
        String key = BotParamType.BUDGET_NEXT_EXPENSE_NAME.getKey();
        for (ParamArg arg : match.getArgs())
            if (arg.getKey().equals(key))
                node.values.put(key, category.getName());
            else
                resolveParam(node, BotParamType.byKey(arg.getKey()));
        node.setProcessedText(match.process(node.values.getRawMap()));

        Answer answer = new Answer();
        answer.setName(answerPrefix + Long.toString(category.getId()));
        answer.setType(BotMessageType.INLINECURRENCY);
        answer.setTypeOnFinish("textCurrency");
        answer.setInlineEditHint("$(type_currency_hint)");
        answer.setInputKey(BotParamType.BUDGET_EXPENSE);

        // Loop back to this call
        answer.setNextOnFinish(sourceNodeName);

        node.setAutoAnswer(answer.getName());
        node.addAnswer(answer);

        node.finish();
        botRunner.queueNode(node);
    }

    /**
     * When the user enters a value during expense iteration, mark that category as entered.
     *
     * @param answerName   The name of the currency input answer.
     * @param answerValue  The value fo the answer input. Must be a valid double. Can be null since the answer can be a quick answer with no value.
     * @param answerPrefix The prefix of the generated answer nodes.
     */
    protected void setExpenseEnteredOnAnswer(@NonNull String answerName, @Nullable String answerValue,
                                             @NonNull String answerPrefix) {
        if (answerName.startsWith(answerPrefix)) {
            if (TextUtils.isEmpty(answerValue))
                // This was the auto answer for the inline answer type, not the text entry
                return;
            try {
                long id = Long.parseLong(answerName.substring(answerPrefix.length(),
                        answerName.length()));
                ExpenseCategoryBotDAO.setEntered(botType, id, true);
            } catch (NumberFormatException e) {
                CrashlyticsHelper.log(this.getClass().getName(), "setExpenseEnteredOnAnswer",
                        "Attempt to parse Expense Category ID from answer name failed");
                CrashlyticsHelper.logException(e);
            }
        }
    }
}
