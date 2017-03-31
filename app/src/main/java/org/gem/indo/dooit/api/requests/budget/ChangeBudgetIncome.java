package org.gem.indo.dooit.api.requests.budget;

/**
 * Created by Wimpie Victor on 2017/03/30.
 */

public class ChangeBudgetIncome {

    private double income;

    public ChangeBudgetIncome(double income) {
        this.income = income;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
