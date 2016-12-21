package org.gem.indo.dooit.models.goal;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by herman on 2016/11/05.
 */

public class Goal {

    // Remote properties
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
    private boolean timeIsUp;
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
    // The id of the predefined Goal. null means custom.
    private Long prototype;
    // New badges are awarded on responses
    @SerializedName("new_badges")
    private List<Badge> newBadges;

    // Local Properties
    private String localImageUri;
    private boolean imageFromProto = false;

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

    private void calculateValue() {
        value = 0;
        for (GoalTransaction trans : transactions)
            value += trans.getValue();
    }

    public String getValueFormatted() {
        return CurrencyHelper.format(value);
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

    public Long getPrototype() {
        return prototype;
    }

    public void setPrototype(Long prototype) {
        this.prototype = prototype;
    }

    public boolean isCustom() {
        return prototype == null;
    }

    public void setTransactions(List<GoalTransaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(GoalTransaction transaction) {
        this.transactions.add(transaction);
        calculateValue();
    }

    public GoalTransaction createTransaction(double value) {
        GoalTransaction trans = new GoalTransaction(DateTime.now(), value);
        addTransaction(trans);
        return trans;
    }

    public boolean canDeposit() {
        return value < target;
    }

    public boolean canWithdraw() {
        return value > 0;
    }

    public boolean isReached() {
        return value >= target;
    }

    public int getWeeksLeft(Utils.ROUNDWEEK rounding) {
        return Utils.weekDiff(endDate.toDate().getTime(), rounding);
    }

    public int getRemainderDaysLeft() {
        return Utils.dayDiff(endDate.toDate().getTime()) - getWeeksLeft(Utils.ROUNDWEEK.DOWN) * 7;
    }

    public List<Badge> getNewBadges() {
        return newBadges;
    }

    public boolean hasNewBadges() {
        return newBadges != null && !newBadges.isEmpty();
    }

    public String getLocalImageUri() {
        return localImageUri;
    }

    public void setLocalImageUri(String localImageUri) {
        this.localImageUri = localImageUri;
    }

    public boolean hasLocalImageUri() {
        return !TextUtils.isEmpty(localImageUri);
    }

    public void setImageFromProto(boolean setImageFromProto) {
        this.imageFromProto = setImageFromProto;
    }

    public boolean imageFromProto() {
        return imageFromProto;
    }

    public void calculateFields() {
        weekCount = (int) (TimeUnit.MILLISECONDS.toDays((long) Math.ceil(endDate.toDate().getTime() - startDate.toDate().getTime()) / 7));
        weeklyTarget = target / weekCount;
    }

    public void setTimeIsUp(boolean missed){ timeIsUp = missed;}

    public boolean getTimeIsUp(){return timeIsUp;}

}
