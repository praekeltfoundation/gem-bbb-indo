package org.gem.indo.dooit.models.goal;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

/**
 * Tests to ensure the integrity of the Goal's Weekly Target and End Date when constructed in
 * different ways.
 * <p>
 * Created by Wimpie Victor on 2017/02/20.
 */

public class GoalConstructionTest {

    /**
     * Goals can be created using a weekly target, but an unknown end date. In this case, the end
     * date must be determined using the start date, Goal target and weekly target.
     * <p>
     * After the Goal has been created, the weekly target must remain consistent. The weekly target
     * is a calculated property.
     *
     * @throws Exception
     */
    @Test
    public void constructionFromWeeklyTarget_consistentPostCreation() throws Exception {
        // The start date will be the current system date.
        LocalDate startDate = new LocalDate(2017, 2, 1);

        // The user inputs a target value and a weekly target
        double target = 1000.0;
        double weeklyTarget = 250.0;

        // The end date is calculated before the Goal is created
        LocalDate endDate = new LocalDate(Goal.endDateFromTarget(startDate.toDate(), target, weeklyTarget));

        // A new Goal is created using the calculated end date
        Goal goal = new Goal();
        goal.setStartDate(startDate);
        goal.setEndDate(endDate);
        goal.setTarget(target);

        // Ensure that the calculated weekly target is the same as supplied
        Assert.assertEquals(weeklyTarget, goal.getWeeklyTarget());
    }
}
