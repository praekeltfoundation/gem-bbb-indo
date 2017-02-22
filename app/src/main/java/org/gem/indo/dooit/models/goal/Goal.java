package org.gem.indo.dooit.models.goal;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.strings.StringHelper;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.date.DefaultToday;
import org.gem.indo.dooit.models.date.Today;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

    transient private Today today = new DefaultToday();

    // Remote Fields //
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

    /**
     * Weekly totals are calculated on backend.
     */
    @SerializedName("weekly_totals")
    private LinkedHashMap<String, Float> weeklyTotals;

    private long user;

    // The id of the predefined Goal. null means custom.
    @SerializedName("prototype")
    private Long prototypeId;

    // New badges are awarded on responses
    @SerializedName("new_badges")
    private List<Badge> newBadges = new ArrayList<>();

    //////////////////
    // Local Fields //
    //////////////////

    private String localImageUri;
    private boolean imageFromProto = false;

    // Server does not allow prototype object in Goal
    // TODO: Ensure that Goal Add Controller still works when a Goal is loaded from persistence during the conversation
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

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Given a starting date, target and weekly target, calculate the end date.
     */
    public static Date endDateFromTarget(Date startDate, double target, double weeklyTarget) {
        double days = weeksFromWeeklyTarget(target, weeklyTarget) * 7.0;
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
        double weeks = getWeeks();
        return weeks == 0.0 ? target : target / weeks;
    }

    public static double weeksFromWeeklyTarget(double target, double weeklyTarget) {
        return weeklyTarget != 0.0 ? target / weeklyTarget : target;
    }

    ////////////////
    // Week Count //
    ////////////////

    public double getWeeks(WeekCalc.Rounding rounding) {
        if (startDate == null || endDate == null)
            return 0.0;
        double weeks = WeekCalc.weekDiff(startDate.toDate(), endDate.toDate(), rounding);
        return weeks == 0.0 ? 1.0 : weeks;
    }

    public double getWeeks() {
        return getWeeks(WeekCalc.Rounding.NONE);
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
        return weeksPast == 0 ? getValue() : getValue() / weeksPast;
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

    public GoalTransaction createTransaction(double value, boolean clamp) {
        return createTransaction(DateTime.now(), value, clamp);
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

    public double getWeeksLeft(WeekCalc.Rounding rounding) {
        if (endDate != null)
            return WeekCalc.weekDiff(today.now(), endDate.toDate(), rounding);
        else
            return 0;
    }

    public int getRemainderDaysLeft() {
        return WeekCalc.remainder(startDate.toDate(), endDate.toDate());
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
        goal.setValue(this.value);
        goal.setTarget(this.target);
        goal.setImageUrl(StringHelper.newString(this.imageUrl));
        goal.setStartDate(new LocalDate(this.startDate));
        goal.setEndDate(new LocalDate(this.endDate));
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

    /////////////
    // Builder //
    /////////////

    public static class GoalCreationError extends RuntimeException {
        public GoalCreationError(String message) {
            super(message);
        }
    }

    /**
     * A Builder to help with the creation of Goals, so that the idiosyncrasies of Goal creation can
     * be handled safely and explicitly.
     */
    public static class Builder {

        public static final String TAG = Goal.Builder.class.getName();

        transient private Today today = new DefaultToday();
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private double target;
        private Double weeklyTarget;

        public Builder() {

        }

        public Goal build() {
            Goal goal = new Goal(today);

            // Name
            goal.setName(name);

            // Target
            goal.setTarget(target);

            // Start Date
            if (startDate == null)
                startDate = new LocalDate(today.now());
            goal.setStartDate(startDate);

            // End Date
            if (endDate != null) {
                CrashlyticsHelper.log(TAG, "build", "Creating Goal from End Date");
                goal.setEndDate(endDate);
            } else if (weeklyTarget != null) {
                CrashlyticsHelper.log(TAG, "build", "Creating Goal from Weekly Target");
                goal.setEndDate(new LocalDate(Goal.endDateFromTarget(startDate.toDate(), target, weeklyTarget)));
            } else {
                throw new GoalCreationError("Unable to build Goal. Neither End Date nor Weekly Target was set.");
            }

            return goal;
        }

        ///////////
        // Today //
        ///////////

        public Goal.Builder setToday(@NonNull Today today) {
            this.today = today;
            return this;
        }

        //////////
        // Name //
        //////////

        public Goal.Builder setName(String name) {
            this.name = name;
            return this;
        }

        ////////////////
        // Start Date //
        ////////////////

        public Goal.Builder setStartDate(@NonNull LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        //////////////
        // End Date //
        //////////////

        public Goal.Builder setEndDate(@NonNull LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Goal.Builder clearEndDate() {
            this.endDate = null;
            return this;
        }

        ////////////
        // Target //
        ////////////

        public Goal.Builder setTarget(double target) {
            this.target = target;
            return this;
        }

        ///////////////////
        // Weekly Target //
        ///////////////////

        public Goal.Builder setWeeklyTarget(double weeklyTarget) {
            this.weeklyTarget = weeklyTarget;
            return this;
        }

        public Goal.Builder clearWeeklyTarget() {
            this.weeklyTarget = null;
            return this;
        }
    }
}