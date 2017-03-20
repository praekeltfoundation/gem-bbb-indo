package org.gem.indo.dooit.models.budget;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Wimpie Victor on 2017/03/03.
 */

public class BudgetTest {

    /**
     * Ensure the default savings is 10% of the Budget's income.
     *
     * @throws Exception
     */
    @Test
    public void defaultSavings_basic() throws Exception {
        Budget budget = new Budget();
        budget.setIncome(70000.0);

        Assert.assertEquals(7000.0, budget.getDefaultSavings());
    }

    @Test
    public void getExpense_basic() throws Exception {
        Budget budget = new Budget();
        budget.setIncome(70000.0);
        budget.addExpense(new Expense("Snacks", 1000.0));
        budget.addExpense(new Expense("Clothes", 2000.0));

        Assert.assertEquals(budget.getExpenseTotal(), 3000.0);
    }
}
