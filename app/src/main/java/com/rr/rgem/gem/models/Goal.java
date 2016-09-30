package com.rr.rgem.gem.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-09-19.
 */
public class Goal {
    private String name;
    private Date startDate;
    private Date endDate;
    private BigDecimal value;
    private String imageName;
    private List<Transaction> transactions;

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


    public BigDecimal getWeeklySavingsGoal() {
        BigDecimal weeks = new BigDecimal(Double.valueOf(endDate.getTime() - startDate.getTime()) / (7*24*60*60*1000));
        BigDecimal weeklySavingsGoal = value.divide(weeks, 8, RoundingMode.HALF_UP);
        return weeklySavingsGoal;
    }

    public BigDecimal getLastWeekSavings() {
        Date current = new Date();
        Date lastWeek = new Date(current.getTime() - (7*24*60*60*1000));

        BigDecimal total = new BigDecimal(0);
        for (Transaction t: this.transactions) {
            if(lastWeek.compareTo(t.date) <= 0)
                total = total.add(t.value);
        }
        return total;
    }


    public BigDecimal getAverageWeeklySavings() {
        Date current = (new GregorianCalendar()).getTime();
        BigDecimal period = new BigDecimal(Double.valueOf(current.getTime() - startDate.getTime()) / (7*24*60*60*1000));

        BigDecimal total = new BigDecimal(0);
        for (Transaction t: this.transactions) {
                total = total.add(t.value);
        }

        BigDecimal average = total.divide(period, 8, RoundingMode.HALF_UP);
        return average;
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
