package org.gem.indo.dooit.models.goal;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.strings.StringHelper;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.date.DefaultToday;
import org.gem.indo.dooit.models.date.Today;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.models.enums.CurrencyCalc;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by herman on 2016/11/05.
 */

public class Goal {

    transient private Today today = new DefaultToday();
    transient private CurrencyCalc currency = CurrencyCalc.IDR;

    // Remote Fields //
    private long id;
    private String name;
    private boolean valueInvalidated = true;
    private boolean weeklyTargetInvalidated = true;
    private double value;
    private double target;
    @SerializedName("initial_savings")
    private double initialAmount;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("start_date")
    private LocalDate startDate;
    @SerializedName("end_date")
    private LocalDate endDate;

    /**
     * The target value the user must save per week. It can be calculated using the the start and
     * end dates, or inputted by the user directly. When the user inputs the weekly target, it is
     * preferable not to change it. However, when calculating the weekly target using date
     * measurements, it should be rounded in such a manner that the user will comfortably achieve
     * their Goal target by the time the end date is reached.
     */
    @SerializedName("weekly_target")
    private double weeklyTarget;

    /*
    This value is only used for internal calculations and does not need to be saved with the goal
     */
    private double dailySavings;

    /*
    Flag to check if the goal daily savings has been rounded
     */
    private boolean hasDailySavingsBeenRounded = false;

    /*
    Number of days before the end date that the goal will be completed due to the rounding of
    daily savings
     */
    private int goalCompletionPriorDays;

    private List<GoalTransaction> transactions = new ArrayList<>();

    /**
     * Weekly totals are calculated on backend. They are counted from 1, and it is assumed that the
     * backend provides the map ordered, even though the JSON standard doesn't guarantee it.
     */
    @SerializedName("weekly_totals")
    private LinkedHashMap<String, Float> weeklyTotals;

    private long user;

    /**
     * The ID of the predefined Goal Prototype. `null` means the user created a custom Goal.
     */
    @SerializedName("prototype")
    private Long prototypeId;

    /**
     * New badges are awarded on responses
     */
    @SerializedName("new_badges")
    private List<Badge> newBadges = new ArrayList<>();

    //////////////////
    // Local Fields //
    //////////////////

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

    private double validatedValue() {
        if (valueInvalidated) {
            calculateValue();
            valueInvalidated = false;
        }
        return value;
    }

    public double getValue() {
        return validatedValue();
    }

    public void setValue(double value) {
        this.value = value;
        valueInvalidated = false;
    }

    public void calculateValue() {
        value = initialAmount;
        for (GoalTransaction trans : transactions)
            value += trans.getValue();
    }

    public String getValueFormatted() {
        return CurrencyHelper.format(validatedValue());
    }

    //////////////////
    // Target Value //
    //////////////////

    public double getTarget() {
        return target - initialAmount;
    }

    public void setTarget(double target) {
        this.target = target;
        valueInvalidated = true;
        weeklyTargetInvalidated = true;
    }

    /**
     * Because the weekly target is rounded, the total of each week's savings might exceed the Goal
     * target. The user will be informed of this.
     *
     * @return The remaining savings, rounded down
     */
    public double getSavingRemainder() {
        double targetSavings = getWeeklyTarget() * getWeeks(WeekCalc.Rounding.NONE);
        return targetSavings > target ? currency.floor(targetSavings - target) : 0.0;
    }

    ///////////////////
    // Already Saved //
    ///////////////////

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double amount) {
        this.initialAmount = amount;
        valueInvalidated = true;
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

    public boolean hasStartDate() {
        return startDate != null;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate The new end date
     * @param recalc  When true, dependant fields such as the weekly target will be recalculated
     */
    public void setEndDate(LocalDate endDate, boolean recalc) {
        this.endDate = endDate;

        if (recalc) {
            valueInvalidated = true;
        }
    }

    public void setEndDate(LocalDate endDate) {
        setEndDate(endDate, true);
    }

    public boolean willReachGoalEarly() { return goalCompletionPriorDays != 0; }

    public int getGoalCompletionPriorDays() { return goalCompletionPriorDays; }

    /**
     * Given a starting date, target and weekly target, calculate the end date. Days are rounded up.
     */
    public static Date endDateFromTarget(Date startDate, double target, double weeklyTarget) {
        double days = Math.ceil(weeksFromWeeklyTarget(target, weeklyTarget) * 7.0);
        return new Date(startDate.getTime() + WeekCalc.daysToMillis(days));
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
        if (weeklyTargetInvalidated) {
            calculateWeeklyTarget();
            weeklyTargetInvalidated = false;
        }
        return weeklyTarget;
    }

    public String getWeeklyTargetFormatted() {
        return CurrencyHelper.format(weeklyTarget);
    }

    /**
     * @param weeklyTarget New weekly target
     * @param recalc       When true, dependant fields such as the end date will be recalculated
     */
    public void setWeeklyTarget(double weeklyTarget, boolean recalc) {
        this.weeklyTarget = weeklyTarget;

        // Update End date according ot weekly target
        if (recalc)
            this.endDate = new LocalDate(endDateFromTarget(startDate.toDate(), target, weeklyTarget));
    }

    public void setWeeklyTarget(double weeklyTarget) {
        setWeeklyTarget(weeklyTarget, true);
    }

    /**
     * Calculates and updates the {@link Goal#weeklyTarget} field
     */
    public void calculateWeeklyTarget() {
        double weeks = getWeeks();

        // There is no validation when setting the initial value, so it can be greater than
        // the goal amount. If this case happens, there doesn't need to be a weekly target
        if (initialAmount >= target) {
            this.weeklyTarget = 0;
            return;
        }

        // Calculate how much needs to be saved per day
        // Divide actual goal amount by number of days.
        double dailySavings = (target - initialAmount) / (weeks * 7);

        // Is the daily savings going to be rounded to the nearest 100
        if (dailySavings % 100 != 0) {
            this.dailySavings = dailySavings = currency.ceil(dailySavings);
            this.hasDailySavingsBeenRounded = true;
        }

        setDailySavings(dailySavings);
        double weeklyTarget = weeks == 0.0 ? target : (dailySavings * 7);
        this.weeklyTarget = currency.ceil(weeklyTarget);

        // If it's possible the user will reach their target before the end date they chose,
        // inform them of this and calculate new end date
        if (this.hasDailySavingsBeenRounded) {
            int daysToCompleteGoal = 0;
            int tempGoalValue = 0;
            while (tempGoalValue < getTarget()) {
                tempGoalValue += dailySavings;
                daysToCompleteGoal++;
            }

            if (daysToCompleteGoal == this.getDays(weeks)) {
                this.hasDailySavingsBeenRounded = false;
            } else {
                this.goalCompletionPriorDays = this.getDays(weeks) - daysToCompleteGoal;
            }
        }
    }

    public static double weeksFromWeeklyTarget(double target, double weeklyTarget) {
        return weeklyTarget != 0.0 ? target / weeklyTarget : target;
    }

    //////////////////
    // Daily savings //
    //////////////////

    private void setDailySavings(double dailySavings){ this.dailySavings = dailySavings; }

    public double getDailySavings() { return this.dailySavings; }

    //////////////////////
    // Early Completion //
    //////////////////////

    public double getEarlyCompleteWeeks() {
        if (hasDailySavingsBeenRounded) {
            if (startDate == null || endDate == null)
                return 0;

            LocalDate earlyCompleteDate = endDate.minusDays(goalCompletionPriorDays);
            double weeks = WeekCalc.weekDiff(startDate.toDate(), earlyCompleteDate.toDate(), WeekCalc.Rounding.DOWN);
            return weeks == 0.0 ? 1.0 : Math.floor(weeks * 10.0) / 10.0;
        } else {
            return 0;
        }
    }

    public int getEarlyCompleteDays() {
        if (hasDailySavingsBeenRounded) {
            LocalDate earlyCompleteDate = endDate.minusDays(goalCompletionPriorDays);
            return WeekCalc.remainder(startDate.toDate(), earlyCompleteDate.toDate());
        } else {
            return 0;
        }
    }

    ///////////////////////////////////////////////
    // Weekly Period from Start Date to End Date //
    ///////////////////////////////////////////////

    public double getWeeks(WeekCalc.Rounding weekRounding) {
        if (startDate == null || endDate == null)
            return 0.0;
        double weeks = WeekCalc.weekDiff(startDate.toDate(), endDate.toDate(), weekRounding);
        // Weeks are *not* rounded down to the first decimal
        return weeks == 0.0 ? 1.0 : weeks;
    }

    public double getWeeks() {
        return getWeeks(WeekCalc.Rounding.NONE);
    }

    public int getDays(double weeks) {
        return (int) (weeks * 7);
    }

    public int getRemainderDays() {
        return WeekCalc.remainder(startDate.toDate(), endDate.toDate());
    }

    ///////////////////////
    // Week Count to Now //
    ///////////////////////

    /**
     * @return The number of weeks since the Goal start to the current date. Used for determining
     * the average saved since the start of the Goal.
     */
    public double getWeeksToNow(WeekCalc.Rounding rounding) {
        return startDate == null ? 0 : WeekCalc.weekDiff(startDate.toDate(), today.now(), rounding);
    }

    ////////////////////
    // Weekly Average //
    ////////////////////

    public double getWeeklyAverage() {
        double weeksPast = getWeeksToNow(WeekCalc.Rounding.NONE);
        return weeksPast == 0 ? validatedValue() : validatedValue() / weeksPast;
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
        valueInvalidated = true;
    }

    /**
     * @param dateTime The datetime on which the transaction occurred.
     * @param value    The monetary value of the transaction
     * @param clamp    When true, and the transaction withdraws enough to put the Goal in the negative,
     *                 the value of the transaction will be clamped so the Goal value will be 0.
     * @return The created transaction.
     */
    public GoalTransaction createTransaction(DateTime dateTime, double value, boolean clamp) {
        if (clamp && this.value + value < 0)
            value = -this.value;

        GoalTransaction trans = new GoalTransaction(dateTime, value);
        addTransaction(trans);
        return trans;
    }

    public GoalTransaction createTransaction(DateTime dateTime, double value) {
        return createTransaction(dateTime, value, true);
    }

    public GoalTransaction createTransaction(double value, boolean clamp) {
        return createTransaction(DateTime.now(), value, clamp);
    }

    public GoalTransaction createTransaction(double value) {
        return createTransaction(value, true);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public boolean canDeposit() {
        return validatedValue() < target;
    }

    public boolean canWithdraw() {
        return validatedValue() > 0;
    }

    public boolean isReached() {
        return validatedValue() >= target;
    }

    ///////////////
    // Time Left //
    ///////////////

    public double getWeeksLeft(WeekCalc.Rounding rounding) {
        if (endDate != null)
            return WeekCalc.weekDiff(today.now(), endDate.toDate(), rounding);
        else
            return 0;
    }

    public int getRemainderDaysLeft() {
        return WeekCalc.remainder(today.now(), endDate.toDate());
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean hasImageUrl(){
        return imageUrl != null;
    }

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

    //////////
    // Copy //
    //////////

    public Goal copy() {
        Goal goal = new Goal();

        goal.setId(this.id);
        goal.setName(StringHelper.newString(this.name));
        goal.setValue(validatedValue());
        goal.setTarget(this.target);
        goal.setWeeklyTarget(this.weeklyTarget, false);
        goal.setDailySavings(this.dailySavings);
        goal.setImageUrl(StringHelper.newString(this.imageUrl));
        goal.setStartDate(new LocalDate(this.startDate));
        goal.setEndDate(new LocalDate(this.endDate), false);
        for (GoalTransaction gt : this.transactions)
            goal.addTransaction(gt.copy());

        goal.setWeeklyTotals(new LinkedHashMap<>(this.getWeeklyTotals()));

        goal.setUser(this.user);
        goal.setPrototypeId(this.prototypeId);
        List<Badge> tempBadges = new ArrayList<>();
        for (Badge badge : this.newBadges)
            tempBadges.add(badge.copy());

        goal.newBadges = tempBadges;
        goal.setLocalImageUri(StringHelper.newString(this.localImageUri));
        //goal.setPrototype(this.prototype.copy());     //Prototype will not necessarily be set

        return goal;
    }
}