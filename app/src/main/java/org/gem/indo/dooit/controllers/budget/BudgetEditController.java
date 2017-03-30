package org.gem.indo.dooit.controllers.budget;

import android.content.Context;

import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.models.enums.BotType;

/**
 * Created by Wimpie Victor on 2017/03/29.
 */

public class BudgetEditController extends DooitBotController {

    public BudgetEditController(Context context) {
        super(context, BotType.BUDGET_EDIT);
    }
}
