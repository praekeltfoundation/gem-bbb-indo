package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.requests.budget.ChangeBudgetIncome;
import org.gem.indo.dooit.api.requests.budget.ChangeBudgetSavings;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.ExpenseCategory;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public interface BudgetAPI {
    @GET("/api/expense-categories/")
    Observable<List<ExpenseCategory>> getExpenseCategories();

    @POST("/api/budgets/")
    Observable<BudgetCreateResponse> upsertBudget(@Body Budget budget);

    @GET("/api/budgets/")
    Observable<List<Budget>> getBudgets();

    @PATCH("/api/budgets/{id}/")
    Observable<Budget> updateBudget(@Path("id") long id, @Body ChangeBudgetIncome income);

    @PATCH("/api/budgets/{id}/")
    Observable<Budget> updateBudget(@Path("id") long id, @Body ChangeBudgetSavings savings);

    @DELETE("/api/expenses/{id}/")
    Observable<EmptyResponse> deleteExpense(@Path("id") long id);
}
