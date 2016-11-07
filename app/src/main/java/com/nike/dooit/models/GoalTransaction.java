package com.nike.dooit.models;

import org.joda.time.DateTime;

/**
 * Created by Wimpie Victor on 2016/11/06.
 */

public class GoalTransaction {

    private DateTime date;
    private double value;

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
}
