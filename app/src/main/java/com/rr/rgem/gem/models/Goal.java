package com.rr.rgem.gem.models;

import android.icu.util.DateInterval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-09-19.
 */
public class Goal {
    String name;
    Date startDate;
    Date endDate;
    BigDecimal value;
    String imageName;
    List<Transaction> transactions;

    public Goal() {
        this.name = "";
        this.startDate = new Date();
        this.endDate = new Date();
        this.value = new BigDecimal(0);
        this.transactions = new ArrayList<Transaction>();
    }

    public Goal(String name) {
        this.name = name;
        this.startDate = new Date();
        this.value = new BigDecimal(0);
        this.transactions = new ArrayList<Transaction>();
    }

    public Goal(String name, BigDecimal value) {
        this.name = name;
        this.startDate = new Date();
        this.value = value;
        this.transactions = new ArrayList<Transaction>();
    }

    public Goal(String name, BigDecimal value, Date start, Date end) {
        this.name = name;
        this.startDate = start;
        this.endDate = end;
        this.value = value;
        this.transactions = new ArrayList<Transaction>();
    }

    public Date getStart() {
        return this.startDate;
    }
    public boolean setStart(Date start) {
        this.startDate = start;
        return true;
    }

    public Date getEnd() {
        return this.endDate;
    }
    public boolean setEnd(Date end) {
        this.endDate = end;
        return true;
    }

    public String getName() {
        return this.name;
    }
    public boolean setName(String name) {
        this.name = name;
        return true;
    }

    public String getImageName() {return this.imageName; }
    public boolean setImageName(String imageName) {
        this.imageName = imageName;
        return true;
    }

    /*
    public BigDecimal getWeeklySavingsGoal() {
        BigDecimal weeks = new BigDecimal((endDate.getTime() - startDate.getTime()) / );
    }

    public BigDecimal getLastWeekSavings() {
        return null;
    }
    */

    public BigDecimal getAverageWeeklySavings() {
        BigDecimal total = new BigDecimal(0);
        for (Transaction t: transactions) {
            total = total.add(t.value);
        }
        Date now = new Date();
        long millisInWeek = 7*24*60*60*1000;
        long delT = (now.getTime() - startDate.getTime());
        delT /= millisInWeek;
        if (((delT % millisInWeek) > 0) || (delT == 0)) {
            delT++;
        }
        return total.divide(new BigDecimal(delT));
    }

    public boolean addTransaction(Date timestamp, BigDecimal value) {
        if (transactions == null) {
            transactions = new ArrayList<Transaction>();
        }
        Transaction t = new Transaction(timestamp, value);
        transactions.add(t);
        return true;
    }

    public boolean addTransaction(Transaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<Transaction>();
        }
        transactions.add(transaction);
        return true;
    }

    public Transaction[] getTransactions() {
        return (Transaction[])this.transactions.toArray();
    }

    public BigDecimal getTotalSaved() {
        BigDecimal total = new BigDecimal(0);
        for (Transaction t: this.transactions) {
            total = total.add(t.value);
        }
        return total;
    }
}
