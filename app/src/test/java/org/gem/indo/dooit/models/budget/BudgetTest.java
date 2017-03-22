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

        Assert.assertEquals(3000.0, budget.getExpenseTotal());
    }

    @Test
    public void getLeftOver_basic() throws Exception {
        Budget budget = new Budget();
        budget.setIncome(100000.0);
        budget.setSavings(30000.0);
        budget.addExpense(new Expense("Snacks", 10000.0));
        budget.addExpense(new Expense("Snacks", 20000.0));

        Assert.assertEquals(40000.0, budget.getLeftOver());
    }
}
