package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

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
    private static String SAVINGS = "budget_create_a_savings";

    @Inject
    BudgetManager budgetManager;

    private Budget budget;
    private BotRunner botRunner;

    public BudgetCreateController(Activity activity, @NonNull BotRunner botRunner,
                                  @Nullable Budget budget) {
        super(activity, BotType.BUDGET_CREATE);
        ((DooitApplication) activity.getApplication()).component.inject(this);

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
                        if (answers.containsKey(INCOME))
                            // Retrieve entered income
                            model.values.put(key, CurrencyHelper.format(Budget.calcDefaultSavings(
                                    Double.parseDouble(answers.get(INCOME).getValue()))));
                        else {
                            // A Node requested the default savings before the income was input.
                            // Unexpected behaviour.
                            model.values.put(key, CurrencyHelper.format(0.0));
                            CrashlyticsHelper.log(TAG, "resolveParam",
                                    "Default savings requested before income entry");
                        }
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
        if (answerLog.containsKey(INCOME))
            budget.setIncome(Double.parseDouble(answerLog.get(INCOME).getValue()));

        // Savings
        if (answerLog.containsKey(SAVING_DEFAULT_ACCEPT))
            // User accepts the default suggested savings
            budget.setSavings(budget.getDefaultSavings());
        else if (answerLog.containsKey(SAVINGS))
            // User entered their own savings amount
            budget.setSavings(Double.parseDouble(answerLog.get(SAVINGS).getValue()));

        // Expenses
        // TODO: Get expenses

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
            public void call(BudgetCreateResponse budgetCreateResponse) {
                // Budget Primary Key is from server
                new BudgetDAO().update(budgetCreateResponse.getBudget());

                // TODO: Badges from budget create
            }
        });
    }
}
