package org.gem.indo.dooit.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Goal {

    private Long id;
    private String name;
    private double value;
    private double target;
    @SerializedName("image_url")
    private String image;
    @SerializedName("start_date")
    private LocalDate startDate;
    @SerializedName("end_date")
    private LocalDate endDate;
    private List<GoalTransaction> transactions = new ArrayList<>();
    @SerializedName("weekly_totals")
    private LinkedHashMap<String, Float> weeklyTotals;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<GoalTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<GoalTransaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(GoalTransaction transaction) {
        this.transactions.add(transaction);
    }

    public GoalTransaction createTransaction(double value) {
        GoalTransaction trans = new GoalTransaction(DateTime.now(), value);
        addTransaction(trans);
        return trans;
    }

    public LinkedHashMap<String, Float> getWeeklyTotals() {
        return weeklyTotals;
    }

    public void setWeeklyTotals(LinkedHashMap<String, Float> weeklyTotals) {
        this.weeklyTotals = weeklyTotals;
    }
}