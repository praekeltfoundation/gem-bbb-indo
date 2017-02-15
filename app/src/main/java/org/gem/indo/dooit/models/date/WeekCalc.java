package org.gem.indo.dooit.models.date;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wimpie Victor on 2017/02/15.
 */

public class WeekCalc {

    /**
     * @param fromDate The starting date
     * @param toDate   The ending date
     * @param round    Indicates whether the weeks should be rounded up or down. The remainder days
     *                 can be found using {@link WeekCalc#remainder(Date, Date)}
     * @return The difference between the two dates measured in weeks.
     */
    public static int weekDiff(Date fromDate, Date toDate, Rounding round) {
        switch (round) {
            case UP:
                return (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()) / 7f);
            case DOWN:
            default:
                return (int) Math.floor(TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()) / 7f);
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
        return dayDiff(fromDate, toDate) - weekDiff(fromDate, toDate, Rounding.DOWN) * 7;
    }

    public enum Rounding {
        UP,
        DOWN
    }
}
