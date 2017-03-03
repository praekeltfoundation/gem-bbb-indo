package org.gem.indo.dooit.models.budget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/03/03.
 */

public class Budget {

    private static double DEFAULT_SAVING_PERCENT = 10.0;

    /**
     * The user's monthly income.
     */
    private double income;

    /**
     * The user's monthly savings amount.
     */
    private double savings;

    private List<Expense> expenses = new ArrayList<>();

    public Budget() {

    }

    ////////////
    // Income //
    ////////////

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }


    /////////////
    // Savings //
    /////////////


    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    /**
     * @return The default savings using the Budget's set income.
     */
    public double getDefaultSavings() {
        return (income / 100.0) * DEFAULT_SAVING_PERCENT;
    }

    //////////////
    // Expenses //
    //////////////

    public List<Expense> getExpenses() {
        return expenses;
    }
}
