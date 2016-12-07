package org.gem.indo.dooit.controllers;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.bot.BaseBotModel;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class DooitBotController extends BotCallback {

    @Inject
    protected Persisted persisted;

    public DooitBotController(Context context) {
        super(context);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
    }

    /**
     * Common parameters.
     *  @param model The bot model that needs the parameter value
     * @param paramType   The key of the parameter
     */
    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        switch (paramType) {
            case USER_USERNAME:
                model.values.put(paramType.getKey(), persisted.getCurrentUser().getUsername());
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }
}
