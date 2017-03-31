package org.gem.indo.dooit.api.requests.budget;

/**
 * Created by Wimpie Victor on 2017/03/30.
 */

public class ChangeBudgetSavings {

    private double savings;

    public ChangeBudgetSavings(double savings) {
        this.savings = savings;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }
}
