package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Goal {

    private String name;
    private double value;
    private double target;
    @SerializedName("image_url")
    private String image;
    @SerializedName("start_date")
    private DateTime startDate;
    @SerializedName("end_date")
    private DateTime endDate;
    private List<GoalTransaction> transactions;
    @SerializedName("weekly_totals")
    private LinkedHashMap<String, Float> weeklyTotals;

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

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public List<GoalTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<GoalTransaction> transactions) {
        this.transactions = transactions;
    }

    public LinkedHashMap<String, Float> getWeeklyTotals() {
        return weeklyTotals;
    }

    public void setWeeklyTotals(LinkedHashMap<String, Float> weeklyTotals) {
        this.weeklyTotals = weeklyTotals;
    }
}
