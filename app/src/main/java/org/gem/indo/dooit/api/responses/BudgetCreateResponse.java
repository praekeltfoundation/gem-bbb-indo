package org.gem.indo.dooit.api.responses;

import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.budget.Budget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/03/07.
 */

public class BudgetCreateResponse {

    private Budget budget;
    private List<Badge> badges = new ArrayList<>();

    ////////////
    // Budget //
    ////////////

    public Budget getBudget() {
        return budget;
    }

    ////////////
    // Badges //
    ////////////

    public List<Badge> getBadges() {
        return badges;
    }
}
