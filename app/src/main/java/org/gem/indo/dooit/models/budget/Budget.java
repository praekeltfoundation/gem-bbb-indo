package org.gem.indo.dooit.models.budget;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Wimpie Victor on 2017/03/03.
 */

public class Budget extends RealmObject {

    private static double DEFAULT_SAVING_PERCENT = 10.0;

    @PrimaryKey
    private long id;

    /**
     * The user's monthly income.
     */
    private double income;

    /**
     * The user's monthly savings amount.
     */
    private double savings;

    private RealmList<Expense> expenses = new RealmList<>();

    public Budget() {

    }

    ////////
    // ID //
    ////////

    public long getId() {
        return id;
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
        return calcDefaultSavings(income);
    }

    public static double calcDefaultSavings(double income) {
        return (income / 100.0) * DEFAULT_SAVING_PERCENT;
    }

    //////////////
    // Expenses //
    //////////////

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public double getExpense() {
        double expense = 0.0;
        for (Expense e : expenses)
            expense += e.getValue();
        return expense;
    }
}
