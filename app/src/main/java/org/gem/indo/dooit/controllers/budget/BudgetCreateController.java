package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/01.
 */

public class BudgetCreateController extends DooitBotController {

    private static String TAG = BudgetCreateController.class.getName();

    private static String INCOME = "income_amount";
    private static String SAVING_DEFAULT_ACCEPT = "budget_create_a_savings_default_accept";
    private static String SAVINGS = "savings_amount";

    @Inject
    BudgetManager budgetManager;

    private MainActivity activity;
    private Budget budget;
    private BotRunner botRunner;

    public BudgetCreateController(Activity activity, @NonNull BotRunner botRunner,
                                  @Nullable Budget budget) {
        super(activity, BotType.BUDGET_CREATE);
        ((DooitApplication) activity.getApplication()).component.inject(this);

        this.activity = (MainActivity) activity;
        this.botRunner = botRunner;

        // A new Budget can be persisted if the conversation is reloaded.
        if (budget == null)
            this.budget = new Budget();
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String name = model.getName();
        String key = paramType.getKey();

        switch (name) {
            case "budget_create_q_savings_default": {
                // Nodes before Budget exists

                Map<String, Answer> answers = botRunner.getAnswerLog();
                switch (paramType) {
                    case BUDGET_DEFAULT_SAVINGS:
                        // Retrieve entered income
                        model.values.put(key, CurrencyHelper.format(Budget.calcDefaultSavings(
                                monthlyIncome(answers))));
                        break;
                    case BUDGET_DEFAULT_SAVING_PERCENT:
                        model.values.put(key, Double.toString(Math.round(Budget.DEFAULT_SAVING_PERCENT)));
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
                            model.values.put(key, CurrencyHelper.format(budget.getExpense()));
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
            case SET_TIP_QUERY:
                tipQuery();
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
        if (answerLog.containsKey(SAVING_DEFAULT_ACCEPT))
            // User accepts the default suggested savings
            budget.setSavings(budget.getDefaultSavings());
        else if (answerLog.containsKey(SAVINGS))
            // User entered their own savings amount
            budget.setSavings(Double.parseDouble(answerLog.get(SAVINGS).getValue()));

        // Expenses
        for (ExpenseCategory category : new ExpenseCategoryBotDAO().findSelected(botType)) {
            budget.addExpense(new Expense(category, 0.0));
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

                // TODO: Badges from budget create
            }
        });
    }

    ////////////
    // Income //
    ////////////

    /**
     * @param answerLog The answer log, which must contain an income input.
     * @return The calculated monthly income. Will be 0.0 if no income input was provided.
     */
    private double monthlyIncome(@NonNull Map<String, Answer> answerLog) {
        if (answerLog.containsKey(INCOME))
            return Double.parseDouble(answerLog.get(INCOME).getValue());
        else {
            CrashlyticsHelper.log(TAG, "monthlyIncome",
                    "Unexpected attempt te get monthly income with no income input in conversation");
            return 0.0;
        }
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

    private boolean validateIncome(String input) {
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

    private boolean validateSavings(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_savings__empty);
            return false;
        }
        try {
            double value = Double.parseDouble(input);

            // Find income defined earlier
            Map<String, Answer> answerLog = botRunner.getAnswerLog();
            Double income = null;
            if (answerLog.containsKey(INCOME))
                income = Double.parseDouble(answerLog.get(INCOME).getValue());

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
}
