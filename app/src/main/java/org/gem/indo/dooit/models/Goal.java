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


    private long id;
    private String name;
    private double value;
    private double target;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("start_date")
    private LocalDate startDate;
    @SerializedName("end_date")
    private LocalDate endDate;
    private List<GoalTransaction> transactions = new ArrayList<>();
    @SerializedName("weekly_totals")
    private LinkedHashMap<String, Float> weeklyTotals;
    @SerializedName("weekly_target")
    private double weeklyTarget;
    @SerializedName("week_count")
    private int weekCount;
    @SerializedName("week_count_to_now")
    private int weekCountToNow;
    @SerializedName("weekly_average")
    private double weeklyAverage;
    private long user;

    // The id of the predefined Goal
    private long prototype;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public LinkedHashMap<String, Float> getWeeklyTotals() {
        return weeklyTotals;
    }

    public void setWeeklyTotals(LinkedHashMap<String, Float> weeklyTotals) {
        this.weeklyTotals = weeklyTotals;
    }

    public double getWeeklyTarget() {
        return weeklyTarget;
    }

    public void setWeeklyTarget(double weeklyTarget) {
        this.weeklyTarget = weeklyTarget;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getWeekCountToNow() {
        return weekCountToNow;
    }

    public void setWeekCountToNow(int weekCountToNow) {
        this.weekCountToNow = weekCountToNow;
    }

    public double getWeeklyAverage() {
        return weeklyAverage;
    }

    public void setWeeklyAverage(double weeklyAverage) {
        this.weeklyAverage = weeklyAverage;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getPrototype() {
        return prototype;
    }

    public void setPrototype(long prototype) {
        this.prototype = prototype;
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

}
