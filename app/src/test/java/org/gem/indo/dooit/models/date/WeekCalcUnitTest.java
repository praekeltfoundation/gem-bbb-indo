package org.gem.indo.dooit.models.date;


import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Wimpie Victor on 2017/02/15.
 */

public class WeekCalcUnitTest {

    @Test
    public void weekDiff_basic() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 15).toDate();

        assertEquals(2.0, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN));
    }

    @Test
    public void weekDiff_roundUp() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 17).toDate();

        assertEquals(3.0, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.UP));
    }

    @Test
    public void weekDiff_roundDown() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 17).toDate();

        assertEquals(2.0, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN));
    }

    /**
     * Ensure that when two days are separated by seven days, a full week will be counted.
     *
     * @throws Exception
     */
    @Test
    public void weekDiff_seven() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 8).toDate();

        assertEquals(1.0, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.UP));
    }

    @Test
    public void dayDiff_basic() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 17).toDate();

        assertEquals(16, WeekCalc.dayDiff(startDate, endDate));
    }

    @Test
    public void remainder_basic() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 18).toDate();

        assertEquals(3, WeekCalc.remainder(startDate, endDate));
    }

    @Test
    public void daysToMillis_basic() throws Exception {
        long millis = WeekCalc.daysToMillis(11.0);

        assertEquals(950400000, millis);
    }

    @Test
    public void daysToMillis_againstJavaTimeUnit() throws Exception {
        long millis = WeekCalc.daysToMillis(11.0);
        long expected = TimeUnit.DAYS.toMillis(11);

        assertEquals(expected, millis);
    }

    @Test
    public void removeTime_basic() throws Exception {
        Calendar cal = Calendar.getInstance();
        // Calendar counts months from 0
        cal.set(2017, 1, 21);
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 21);
        cal.set(Calendar.SECOND, 37);
        cal.set(Calendar.MILLISECOND, 99);

        Date expected = new LocalDate(2017, 2, 21).toDate();

        assertEquals(expected, WeekCalc.removeTime(cal.getTime()));
    }
}
