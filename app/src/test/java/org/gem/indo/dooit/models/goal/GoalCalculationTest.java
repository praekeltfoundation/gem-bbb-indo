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
    public void getWeeksToNow_basic() throws Exception {
        final Today today = Mockito.mock(Today.class);
        Mockito.when(today.now()).thenReturn(new LocalDate(2017, 2, 8).toDate());

        Goal goal = new Goal(today);
        goal.setStartDate(new LocalDate(2017, 2, 1));
        goal.setEndDate(new LocalDate(2017, 2, 16));

        Assert.assertEquals(1, goal.getWeekCountToNow(WeekCalc.Rounding.UP));
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
    public void getWeeklyTarget_basic() throws Exception {
        Goal goal = new Goal();
        goal.setStartDate(new LocalDate(2017, 2, 1));
        goal.setEndDate(new LocalDate(2017, 3, 1));
        goal.setTarget(1000.0);

        Assert.assertEquals(250.0, goal.getWeeklyTarget());
    }

    @Test
    public void getWeeklyAverage_basic() throws Exception {
        final Today today = Mockito.mock(Today.class);
        Mockito.when(today.now()).thenReturn(new LocalDate(2017, 2, 15).toDate());

        Goal goal = new Goal(today);
        goal.setTarget(10000);
        goal.setStartDate(new LocalDate(2017, 2, 1));
        goal.setEndDate(new LocalDate(2017, 2, 28));

        // Week 1
        goal.createTransaction(new LocalDate(2017, 2, 2).toDateTimeAtCurrentTime(), 200.0, true);

        // Week 2
        goal.createTransaction(new LocalDate(2017, 2, 9).toDateTimeAtCurrentTime(), 200.0, true);
        goal.createTransaction(new LocalDate(2017, 2, 10).toDateTimeAtCurrentTime(), 200.0, true);

        Assert.assertEquals(300.0, goal.getWeeklyAverage());
    }

    @Test
    public void weeksFromWeeklyTarget_basic() throws Exception {
        Goal goal = new Goal();
        goal.setStartDate(new LocalDate(2017, 2, 1));

        double target = 1000.0;
        double weeklyTarget = 250.0;

        Assert.assertEquals(4, Goal.weeksFromWeeklyTarget(target, weeklyTarget));
    }
}
