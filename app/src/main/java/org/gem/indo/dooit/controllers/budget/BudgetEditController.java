package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/29.
 */

public class BudgetEditController extends BudgetBotController {

    private static final String TAG = BudgetEditController.class.getName();

    @Inject
    BudgetManager budgetManager;

    public BudgetEditController(Activity activity, BotRunner botRunner, @NonNull Budget budget) {
        super(activity, BotType.BUDGET_EDIT, activity, botRunner);
        ((DooitApplication) activity.getApplication()).component.inject(this);

        this.budget = budget;
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            default:
                super.onCall(key, answerLog, model);
        }
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case UPDATE_BUDGET_INCOME:
                updateIncome(answerLog, listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
        }
    }

    private void updateIncome(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        if (answerLog.containsKey(INCOME_INPUT)) {
            try {
                double income = Double.parseDouble(answerLog.get(INCOME_INPUT).getValue());
                budgetManager.updateBudget(budget.getId(), income, new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {

                    }
                }).doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        notifyDone(listener);
                    }
                }).subscribe(new Action1<Budget>() {
                    @Override
                    public void call(Budget budget) {
                        new BudgetDAO().update(budget);
                        BudgetEditController.this.budget = budget;
                    }
                });
            } catch (NumberFormatException e) {
                CrashlyticsHelper.log(TAG, "updateIncome",
                        "Could not parse income from conversation");
                CrashlyticsHelper.logException(e);
            }
        } else {
            Context context = getContext();
            if (context != null)
                Toast.makeText(context, R.string.budget_edit_err_income__not_found, Toast.LENGTH_SHORT).show();
            listener.onDone();
        }
    }

    private void updateSavings(Map<String, Answer> answerLog, final OnAsyncListener listener) {

    }
}
