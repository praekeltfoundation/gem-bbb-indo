package org.gem.indo.dooit.models.date;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wimpie Victor on 2017/02/15.
 */

public class WeekCalc {

    /**
     * The number of milliseconds in a day.
     */
    private static final double MILLIS_PER_DAY = 86400000.0;

    /**
     * @param fromDate The starting date
     * @param toDate   The ending date
     * @param round    Indicates whether the weeks should be rounded up or down. The remainder days
     *                 can be found using {@link WeekCalc#remainder(Date, Date)}
     * @return The difference between the two dates measured in weeks.
     */
    public static double weekDiff(Date fromDate, Date toDate, Rounding round) {
        switch (round) {
            case UP:
                return Math.ceil(TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()) / 7.0);
            case DOWN:
                return Math.floor(TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()) / 7.0);
            case NONE:
            default:
                return TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()) / 7.0;
        }
    }

    /**
     * @param fromDate The starting date
     * @param toDate   The ending date
     * @return The difference between the two dates in days.
     */
    public static int dayDiff(Date fromDate, Date toDate) {
        return (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()));
    }

    /**
     * @param fromDate The starting date
     * @param toDate   The ending date
     * @return The number of days remaining after a the week difference has been calculated and
     * round down.
     */
    public static int remainder(Date fromDate, Date toDate) {
        return dayDiff(fromDate, toDate) - (int) (weekDiff(fromDate, toDate, Rounding.DOWN) * 7.0);
    }

    public static long daysToMillis(double days) {
        return Math.round(days * MILLIS_PER_DAY);
    }

    /**
     * Helper to Set the time part (hours, minutes, seconds, milliseconds) of a {@link Date} object
     * to 0.
     *
     * @param datetime A date object that's not on midnight.
     * @return A new date object which contains only the date part.
     */
    public static Date removeTime(Date datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public enum Rounding {
        UP,
        DOWN,
        NONE // Not rounded
    }
}
