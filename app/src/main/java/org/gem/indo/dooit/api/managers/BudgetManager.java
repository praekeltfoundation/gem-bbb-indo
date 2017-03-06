package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.BudgetAPI;
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
}
