package org.gem.indo.dooit.models.goal;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.helpers.strings.StringHelper;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.date.DefaultToday;
import org.gem.indo.dooit.models.date.Today;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by herman on 2016/11/05.
 */

public class Goal {

    private Today today = new DefaultToday();

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
    @SerializedName("prototype")
    private Long prototypeId;
    // New badges are awarded on responses
    @SerializedName("new_badges")
    private List<Badge> newBadges = new ArrayList<>();

    // Local Properties
    private String localImageUri;
    private boolean imageFromProto = false;
    // Server does not allow prototype object in Goal
    transient private GoalPrototype prototype;

    //////////////////
    // Constructors //
    //////////////////

    public Goal() {
        // Empty Constructor
    }

    public Goal(Today today) {
        this.today = today;
    }

    ////////
    // ID //
    ////////

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //////////
    // Name //
    //////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasName() {
        return !StringHelper.isEmpty(name);
    }

    ///////////////////
    // Current Value //
    ///////////////////

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

    //////////////////
    // Target Value //
    //////////////////

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

    ///////////
    // Dates //
    ///////////

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
        updateWeeklyTarget();
    }

    private void updateEndDate() {
        // This will be called if the weekly target has been edited

        // calculate weekcount
        double stillNeeded = target - value;
        weekCount = (int) Math.ceil(stillNeeded / weeklyTarget);

        // calculate end date
        endDate = startDate.plusWeeks(weekCount);
    }

    ////////////////////////////
    // Weekly Total Aggregate //
    ////////////////////////////

    public LinkedHashMap<String, Float> getWeeklyTotals() {
        return weeklyTotals;
    }

    public void setWeeklyTotals(LinkedHashMap<String, Float> weeklyTotals) {
        this.weeklyTotals = weeklyTotals;
    }

    ///////////////////
    // Weekly Target //
    ///////////////////

    public double getWeeklyTarget() {
        int weeks = getWeeks();
        if (weeks == 0)
            return value;
        return value / weeks;
    }

    public void setWeeklyTarget(double weeklyTarget) {
        this.weeklyTarget = weeklyTarget;
        updateEndDate();
    }

    public static Date endDateFromWeeklyTarget(double weeklyTarget) {
        // TODO: Calculate end date using provided weekly target
        return new Date();
    }

    ////////////////
    // Week Count //
    ////////////////

    public int getWeeks() {
        if (startDate == null || endDate == null)
            return 0;
        int weeks = WeekCalc.weekDiff(startDate.toDate(), endDate.toDate(), WeekCalc.Rounding.UP);
        if (weeks == 0)
            return 1;
        else
            return weeks;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    ///////////////////////
    // Week Count to Now //
    ///////////////////////

    /**
     * @return The number of weeks since the Goal start to the current date. Used for determining
     * the average saved since the start of the Goal.
     */
    public int getWeekCountToNow() {
        return weekCountToNow;
    }

    public void setWeekCountToNow(int weekCountToNow) {
        this.weekCountToNow = weekCountToNow;
    }

    ////////////////////
    // Weekly Average //
    ////////////////////

    public double getWeeklyAverage() {
        return weeklyAverage;
    }

    public void setWeeklyAverage(double weeklyAverage) {
        this.weeklyAverage = weeklyAverage;
    }

    //////////
    // User //
    //////////

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    ////////////////////
    // Goal Prototype //
    ////////////////////

    public Long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(Long prototypeId) {
        this.prototypeId = prototypeId;
    }

    public boolean isCustom() {
        return prototypeId == null;
    }

    public GoalPrototype getPrototype() {
        return prototype;
    }

    public void setPrototype(GoalPrototype prototype) {
        this.prototype = prototype;
    }

    public boolean hasPrototype() {
        return prototype != null;
    }

    //////////////////
    // Transactions //
    //////////////////

    public List<GoalTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<GoalTransaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(GoalTransaction transaction) {
        this.transactions.add(transaction);
        calculateValue();
    }

    /**
     * @param value The monetary value of the transaction
     * @param clamp When true, and the transaction withdraws enough to put the Goal in the negative,
     *              the value of the transaction will be clamped so the Goal value will be 0.
     * @return The created transaction.
     */
    public GoalTransaction createTransaction(double value, boolean clamp) {
        if (clamp && this.value + value < 0)
            value = -this.value;

        GoalTransaction trans = new GoalTransaction(DateTime.now(), value);
        addTransaction(trans);
        return trans;
    }

    public GoalTransaction createTransaction(double value) {
        return createTransaction(value, true);
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

    ///////////////
    // Time Left //
    ///////////////

    public int getWeeksLeft(WeekCalc.Rounding rounding) {
        if (endDate != null)
            return WeekCalc.weekDiff(today.now(), endDate.toDate(), rounding);
        else
            return 0;
    }

    public int getRemainderDaysLeft() {
        return WeekCalc.dayDiff(today.now(), endDate.toDate()) - getWeeksLeft(WeekCalc.Rounding.DOWN) * 7;
    }

    public boolean isMissed() {
        return new Date().after(endDate.toDate());
    }

    ////////////
    // Badges //
    ////////////

    public List<Badge> getNewBadges() {
        return newBadges;
    }

    public boolean hasNewBadges() {
        return newBadges != null && !newBadges.isEmpty();
    }

    public void addNewBadges(Collection<Badge> badges) {
        newBadges.addAll(badges);
    }

    ///////////////
    // Image Uri //
    ///////////////

    public String getLocalImageUri() {
        return localImageUri;
    }

    public void setLocalImageUri(String localImageUri) {
        this.localImageUri = localImageUri;
    }

    public boolean hasLocalImageUri() {
        return !StringHelper.isEmpty(localImageUri);
    }

    public void setImageFromProto(boolean setImageFromProto) {
        this.imageFromProto = setImageFromProto;
    }

    public boolean imageFromProto() {
        return imageFromProto;
    }

    // TODO: Remove after all fields have been turned into properties
    public void calculateFields() {
        if (endDate != null) {
            updateWeeklyTarget();
        } else if (weeklyTarget != 0) {
            updateEndDate();
        }
    }

    private void updateWeeklyTarget() {
        weekCount = (int) (TimeUnit.MILLISECONDS.toDays((long) Math.ceil(endDate.toDate().getTime() - startDate.toDate().getTime()) / 7));
        weeklyTarget = (target - value) / weekCount;
    }


    public Goal copy() {
        Goal goal = new Goal();

        goal.setId(this.id);
        goal.setName(StringHelper.newString(this.name));
        goal.setValue(this.value);
        goal.setTarget(this.target);
        goal.setImageUrl(StringHelper.newString(this.imageUrl));
        goal.setStartDate(new LocalDate(this.startDate));
        goal.setEndDate(new LocalDate(this.endDate));
        for (GoalTransaction gt : this.transactions) {
            goal.addTransaction(gt.copy());
        }
        goal.setWeeklyTotals(new LinkedHashMap<>(this.getWeeklyTotals()));
        goal.setWeeklyTarget(this.weeklyTarget);
        goal.setWeekCount(this.weekCount);
        goal.setWeekCountToNow(this.weekCountToNow);
        goal.setWeeklyAverage(this.weeklyAverage);
        goal.setUser(this.user);
        goal.setPrototypeId(this.prototypeId);
        List<Badge> tempBadges = new ArrayList<>();
        for (Badge badge : this.newBadges) {
            tempBadges.add(badge.copy());
        }
        goal.newBadges = tempBadges;
        goal.setLocalImageUri(StringHelper.newString(this.localImageUri));
        //goal.setPrototype(this.prototype.copy());     //Prototype will not necessarily be set

        return goal;
    }
}