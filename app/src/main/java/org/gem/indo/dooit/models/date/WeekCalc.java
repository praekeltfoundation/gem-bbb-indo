package org.gem.indo.dooit.models.date;

import org.gem.indo.dooit.helpers.Utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wimpie Victor on 2017/02/15.
 */

public class WeekCalc {

    public static int weekDiff(long time, Utils.ROUNDWEEK round) {
        switch (round) {
            case UP:
                return (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(time - System.currentTimeMillis()) / 7.0f);
            case DOWN:
            default:
                return (int) Math.floor(TimeUnit.MILLISECONDS.toDays(time - System.currentTimeMillis()) / 7.0f);

        }
    }

    /**
     * @return The number of weeks difference
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

    public static int dayDiff(Date fromDate, Date toDate) {
        return (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(toDate.getTime() - fromDate.getTime()));
    }

    public enum Rounding {
        UP,
        DOWN
    }
}
