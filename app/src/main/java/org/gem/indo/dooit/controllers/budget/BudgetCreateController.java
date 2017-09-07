package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/01.
 */

public class BudgetCreateController extends BudgetBotController {

    private static String TAG = BudgetCreateController.class.getName();

    private static String SAVING_DEFAULT_ACCEPT = "budget_create_a_savings_default_accept";
    private static String EXPENSE_QUESTION_PREFIX = "budget_create_q_expense_value_";
    private static String EXPENSE_ANSWER_PREFIX = "budget_create_a_expense_value_";
    private static String EXPENSE_STOP = "budget_create_q_expense_stop";

    @Inject
    BudgetManager budgetManager;

    @Inject
    Persisted persisted;

    public BudgetCreateController(Activity activity, @NonNull BotRunner botRunner) {
        super(activity, BotType.BUDGET_CREATE, activity, botRunner);
        ((DooitApplication) activity.getApplication()).component.inject(this);

        // A new Budget can be persisted if the conversation is reloaded.
        budget = new BudgetDAO().findFirst();
    }

    @Override
    public boolean shouldSkip(BaseBotModel model) {
        String name = model.getName();
        switch (name) {
            case "budget_create_add_expense":
                return ExpenseCategoryBotDAO.hasNext(botType);
            default:
                return super.shouldSkip(model);
        }
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        switch (inputType) {
            case BUDGET_EXPENSE: {
                setExpenseEnteredOnAnswer(answer.getName(), answer.getValue(), EXPENSE_ANSWER_PREFIX);
                break;
            }
            default:
                super.onAnswerInput(inputType, answer);
        }
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String name = model.getName();
        String key = paramType.getKey();

        // Expense Loop
        if (paramType == BotParamType.BUDGET_NEXT_EXPENSE_NAME) {
            ExpenseCategory category = ExpenseCategoryBotDAO.findNext(botType);
            if (category != null)
                model.values.put(key, category.getName());
        }

        switch (name) {
            case "budget_create_q_savings_default":
            case "budget_create_q_expense_01":
            case "budget_create_q_expense_02":
            case "budget_create_q_expense_03":
            case "budget_create_q_expense_summary": {
                // Nodes before Budget exists

                Map<String, Answer> answers = botRunner.getAnswerLog();
                switch (paramType) {
                    case BUDGET_INCOME:
                        model.values.put(key, CurrencyHelper.format(monthlyIncome(answers)));
                        break;
                    case BUDGET_SAVINGS:
                        model.values.put(key, CurrencyHelper.format(monthlySavings(answers)));
                        break;
                    case BUDGET_REMAINING_EXPENSES:
                        model.values.put(key, CurrencyHelper
                                .format(monthlyIncome(answers) - monthlySavings(answers)));
                        break;
                    case BUDGET_DEFAULT_SAVINGS:
                        // Retrieve entered income
                        model.values.put(key, CurrencyHelper.format(Budget.calcDefaultSavings(
                                monthlyIncome(answers))));
                        break;
                    case BUDGET_DEFAULT_SAVING_PERCENT:
                        model.values.put(key, Double.toString(Math.round(Budget.DEFAULT_SAVING_PERCENT)));
                        break;
                    case BUDGET_TOTAL_EXPENSES:
                        model.values.put(key, CurrencyHelper.format(totalExpenseValue(answers)));
                        break;
                    case BUDGET_TOTAL_EXPENSES_REMAINDER:
                        model.values.put(key, CurrencyHelper
                                .format(monthlyIncome(answers) - monthlySavings(answers) - totalExpenseValue(answers)));
                        break;
                }
                break;
            }

            default:
                // Nodes after Budget exists
                switch (paramType) {
                    case BUDGET_INCOME:
                        if (budget != null)
                            model.values.put(key, CurrencyHelper.format(budget.getIncome()));
                        break;
                    case BUDGET_SAVINGS:
                        if (budget != null)
                            model.values.put(key, CurrencyHelper.format(budget.getSavings()));
                        break;
                    case BUDGET_EXPENSE:
                        if (budget != null)
                            model.values.put(key, CurrencyHelper.format(budget.getExpenseTotal()));
                        break;
                    case BUDGET_DEFAULT_SAVINGS:
                        if (budget != null)
                            model.values.put(key, CurrencyHelper.format(budget.getDefaultSavings()));
                        break;
                    default:
                        super.resolveParam(model, paramType);
                }
                break;
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case ADD_EXPENSE:
                addNextExpense(EXPENSE_QUESTION_PREFIX, EXPENSE_ANSWER_PREFIX,
                        "budget_create_add_expense", EXPENSE_STOP, "budget_create_q_expense_summary");
                break;
            case CLEAR_EXPENSES_STATE:
                clearExpenseState();
                break;
            case CLEAR_EXPENSES:
                clearExpenses();
                break;
            case BRANCH_BUDGET_FINAL:
                branchBudgetFinal(answerLog);
                break;
            case BRANCH_BUDGET_GOALS:
                branchBudgetGoals(answerLog);
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case DO_CREATE:
                doCreate(answerLog, listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
                break;
        }
    }

    /**
     * Sends the new Budget to the server.
     */
    private void doCreate(@NonNull Map<String, Answer> answerLog,
                          @NonNull final OnAsyncListener listener) {

        Budget budget = new Budget();

        // Income
        budget.setIncome(monthlyIncome(answerLog));

        // Savings
        budget.setSavings(monthlySavings(answerLog));

        // Expenses
        for (ExpenseCategory category : new ExpenseCategoryBotDAO().findSelected(botType)) {
            String key = EXPENSE_ANSWER_PREFIX + Long.toString(category.getId());
            double expense = 0.0;
            if (answerLog.containsKey(key))
                expense = Double.parseDouble(answerLog.get(key).getValue());

            budget.addExpense(new Expense(category, expense));
        }

        // Upload Budget to server
        budgetManager.upsertBudget(budget, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
            }
        }).subscribe(new Action1<BudgetCreateResponse>() {
            @Override
            public void call(BudgetCreateResponse response) {
                // Budget Primary Key is from server
                new BudgetDAO().update(response.getBudget());

                // Store a reference to the Budget as it was received
                BudgetCreateController.this.budget = response.getBudget();

                persisted.saveNewBudgetBadges(response.getBadges());
            }
        });
    }

    private void clearExpenseState() {
        ExpenseCategoryBotDAO.cleatState(botType);
    }

    private void clearExpenses() {
        new ExpenseCategoryBotDAO().clear(botType);
    }

    private void branchBudgetFinal(Map<String, Answer> answerLog) {
        if (budget == null)
            return;

        double income = budget.getIncome();
        double savings = budget.getSavings();
        double expenses = budget.getExpenseTotal();

        if (income >= savings + expenses) {
            Node node = new Node();
            node.setName("budget_create_budget_positive");
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext("budget_create_q_final_positive");
            node.finish();

            botRunner.queueNode(node);
        } else {
            Node node = new Node();
            node.setName("budget_create_budget_negative");
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext("budget_create_q_final_negative_01");
            node.finish();

            botRunner.queueNode(node);
        }
    }

    private void branchBudgetGoals(Map<String, Answer> answerLog) {
        Node node = new Node();
        node.setName("Hand_over_Node");
        node.setType(BotMessageType.DUMMY);

        List<Goal> goals = persisted.loadConvoGoals(botType);
        if (goals.size() > 0) {
            node.setAutoNext("budget_create_q_final_positive_has_goals");
        } else {
            node.setAutoNext("budget_create_q_final_positive_no_goals");
        }
        botRunner.queueNode(node);
    }

    ////////////
    // Income //
    ////////////

    /**
     * @param answerLog The answer log, which must contain an income input.
     * @return The calculated monthly income. Will be 0.0 if no income input was provided.
     */
    private double monthlyIncome(@NonNull Map<String, Answer> answerLog) {
        if (answerLog.containsKey(INCOME_INPUT))
            return Double.parseDouble(answerLog.get(INCOME_INPUT).getValue());
        else {
            CrashlyticsHelper.log(TAG, "monthlyIncome",
                    "Unexpected attempt te get monthly income with no income input in conversation");
            return 0.0;
        }
    }

    /////////////
    // Savings //
    /////////////

    private double monthlySavings(@NonNull Map<String, Answer> answerLog) {
        try {
            if (answerLog.containsKey(SAVING_DEFAULT_ACCEPT))
                // User accepts the default suggested savings
                if (answerLog.containsKey(INCOME_INPUT))
                    return Budget.calcDefaultSavings(Double.parseDouble(answerLog.get(INCOME_INPUT).getValue()));
                else
                    return 0.0;
            else if (answerLog.containsKey(SAVINGS_INPUT))
                // User entered their own savings amount
                return Double.parseDouble(answerLog.get(SAVINGS_INPUT).getValue());
            else
                return 0.0;
        } catch (NumberFormatException e) {
            CrashlyticsHelper.logException(e);
            return 0.0;
        }
    }

    //////////////
    // Expenses //
    //////////////

    private double totalExpenseValue(Map<String, Answer> answerLog) {
        double total = 0.0;
        for (ExpenseCategory category : new ExpenseCategoryBotDAO().findSelected(botType)) {
            String key = EXPENSE_ANSWER_PREFIX + Long.toString(category.getId());
            if (answerLog.containsKey(key))
                total += Double.parseDouble(answerLog.get(key).getValue());
        }
        return total;
    }

    ////////////////
    // Validation //
    ////////////////

    @Override
    public boolean validate(String name, String input) {
        if (name.startsWith(EXPENSE_ANSWER_PREFIX))
            return validateExpense(input);
        return super.validate(name, input);
    }

}
