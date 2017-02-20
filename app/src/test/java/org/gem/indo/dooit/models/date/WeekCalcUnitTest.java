package org.gem.indo.dooit.models.date;


import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Wimpie Victor on 2017/02/15.
 */

public class WeekCalcUnitTest {

    @Test
    public void weekDiff_basic() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 15).toDate();

        assertEquals(2, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN));
    }

    @Test
    public void weekDiff_roundUp() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 17).toDate();

        assertEquals(3, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.UP));
    }

    @Test
    public void weekDiff_roundDown() throws Exception {
        Date startDate = new LocalDate(2017, 2, 1).toDate();
        Date endDate = new LocalDate(2017, 2, 17).toDate();

        assertEquals(2, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN));
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

        assertEquals(1, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.UP));
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
}
