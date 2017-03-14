package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.ExpenseCategory;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public interface BudgetAPI {
    @GET("/api/expense-categories/")
    Observable<List<ExpenseCategory>> getExpenseCategories();

    @POST("/api/budgets/")
    Observable<BudgetCreateResponse> upsertBudget(@Body Budget budget);
}
