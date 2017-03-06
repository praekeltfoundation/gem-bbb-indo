package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.models.budget.ExpenseCategory;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public interface BudgetAPI {
    @GET("/api/expense-categories/")
    public Observable<List<ExpenseCategory>> getExpenseCategories();
}
