package org.gem.indo.dooit.models.goal;

import junit.framework.Assert;

import org.gem.indo.dooit.models.date.Today;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests to ensure that Goal weekly targets are calculated as expected.
 * <p>
 * Created by Wimpie Victor on 2017/02/15.
 */

public class GoalCalculationTest {

    @Test
    public void getWeeks_basic() throws Exception {
        Goal goal = new Goal();
        goal.setStartDate(new LocalDate(2017, 2, 1));
        goal.setEndDate(new LocalDate(2017, 2, 16));

        Assert.assertEquals(3, goal.getWeeks());
    }

    @Test
    @Ignore("Todo")
    public void getWeeksToNow_basic() throws Exception {

    }

    @Test
    public void getWeeksLeft_basic() throws Exception {
        final Today today = Mockito.mock(Today.class);
        Mockito.when(today.now()).thenReturn(new LocalDate(2017, 2, 8).toDate());

        Goal goal = new Goal(today);
        goal.setStartDate(new LocalDate(2017, 2, 1));
        goal.setEndDate(new LocalDate(2017, 2, 16));

        Assert.assertEquals(2, goal.getWeeksLeft(WeekCalc.Rounding.UP));
    }

    @Test
    @Ignore("Todo")
    public void endDateFromWeeklyTarget() throws Exception {

    }
}
