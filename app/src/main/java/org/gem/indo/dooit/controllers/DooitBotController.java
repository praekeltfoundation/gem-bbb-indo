package org.gem.indo.dooit.controllers;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class DooitBotController extends BotController {

    @Inject
    protected Persisted persisted;

    public DooitBotController(Context context, BotType botType) {
        super(context, botType);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);

    }

    /**
     * Common parameters.
     *
     * @param model     The bot model that needs the parameter value
     * @param paramType The key of the parameter
     */
    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();

        switch (paramType) {
            case USER_USERNAME:
                if (persisted.getCurrentUser() != null)
                    model.values.put(paramType.getKey(), persisted.getCurrentUser().getUsername());
                break;
            case USER_PREFERRED_NAME:
                if (persisted.getCurrentUser() != null)
                    model.values.put(paramType.getKey(), persisted.getCurrentUser().getPreferredName());
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }
}
