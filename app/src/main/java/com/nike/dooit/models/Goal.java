package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by herman on 2016/11/05.
 */

public class Goal {

    private String name;
    private double value;
    private String image;
    @SerializedName("start_date") private DateTime startDate;
    @SerializedName("end_date") private DateTime endDate;

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
}
