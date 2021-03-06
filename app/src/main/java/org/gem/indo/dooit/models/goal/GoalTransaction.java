package org.gem.indo.dooit.models.goal;

import org.joda.time.DateTime;

/**
 * Created by Wimpie Victor on 2016/11/06.
 */

public class GoalTransaction {

    private DateTime date;
    private double value;

    public GoalTransaction copy(){
        return new GoalTransaction(new DateTime(this.date), this.value);
    }

    public GoalTransaction(double value) {
        this(DateTime.now(), value);
    }

    public GoalTransaction(DateTime date, double value) {
        this.date = date;
        this.value = value;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isDeposit() {
        return value > 0;
    }

    public boolean isWithdraw() {
        return value <= 0;
    }
}
