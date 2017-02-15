package org.gem.indo.dooit.models.date;


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
        Date startDate = new GregorianCalendar(2017, 2, 1).getTime();
        Date endDate = new GregorianCalendar(2017, 2, 15).getTime();

        assertEquals(2, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN));
    }

    @Test
    public void weekDiff_roundUp() throws Exception {
        Date startDate = new GregorianCalendar(2017, 2, 1).getTime();
        Date endDate = new GregorianCalendar(2017, 2, 17).getTime();

        assertEquals(3, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.UP));
    }

    @Test
    public void weekDiff_roundDown() throws Exception {
        Date startDate = new GregorianCalendar(2017, 2, 1).getTime();
        Date endDate = new GregorianCalendar(2017, 2, 17).getTime();

        assertEquals(2, WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN));
    }

    @Test
    public void dayDiff_basic() throws Exception {
        Date startDate = new GregorianCalendar(2017, 2, 1).getTime();
        Date endDate = new GregorianCalendar(2017, 2, 17).getTime();

        assertEquals(16, WeekCalc.dayDiff(startDate, endDate));
    }
}
