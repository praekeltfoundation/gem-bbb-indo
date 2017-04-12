package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.BudgetAPI;
import org.gem.indo.dooit.api.requests.budget.ChangeBudgetIncome;
import org.gem.indo.dooit.api.requests.budget.ChangeBudgetSavings;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.ExpenseCategory;

import java.util.List;

import rx.Observable;


/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public class BudgetManager extends DooitManager {

    private final BudgetAPI budgetAPI;

    public BudgetManager(Application application) {
        super(application);
        budgetAPI = retrofit.create(BudgetAPI.class);
    }

    public Observable<List<ExpenseCategory>> retrieveExpenseCategories(DooitErrorHandler errorHandler) {
        return useNetwork(budgetAPI.getExpenseCategories(), errorHandler);
    }

    public Observable<BudgetCreateResponse> upsertBudget(Budget budget, DooitErrorHandler errorHandler) {
        return useNetwork(budgetAPI.upsertBudget(budget), errorHandler);
    }

    public Observable<List<Budget>> retrieveBudgets(DooitErrorHandler errorHandler) {
        return useNetwork(budgetAPI.getBudgets(), errorHandler);
    }

    public Observable<BudgetCreateResponse> updateBudgetIncome(long budgetId, double income, DooitErrorHandler errorHandler) {
        return useNetwork(budgetAPI.updateBudget(budgetId, new ChangeBudgetIncome(income)), errorHandler);
    }

    public Observable<BudgetCreateResponse> updateBudgetSavings(long budgetId, double savings, DooitErrorHandler errorHandler) {
        return useNetwork(budgetAPI.updateBudget(budgetId, new ChangeBudgetSavings(savings)), errorHandler);
    }

    public Observable<EmptyResponse> deleteExpense(long expenseId, DooitErrorHandler errorHandler) {
        return useNetwork(budgetAPI.deleteExpense(expenseId), errorHandler);
    }
}
