package org.gem.indo.dooit.controllers;

import android.content.Context;
import android.text.TextUtils;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public abstract class DooitBotController extends BotController {

    @Inject
    protected UserManager userManager;

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
                    model.values.put(key, persisted.getCurrentUser().getUsername());
                break;
            case USER_PREFERRED_NAME:
                if (persisted.getCurrentUser() != null)
                    model.values.put(key, persisted.getCurrentUser().getPreferredName());
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }

    @Override
    public boolean shouldSkip(BaseBotModel model) {
        User user = persisted.getCurrentUser();
        if (user != null && user.hasFirstName())
            // Skip asking for a first name
            if (model.getName().equals("goal_add_q_greeting_username")
                    || model.getName().equals("goal_add_q_first_name"))
                return true;
        return super.shouldSkip(model);
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        switch (inputType) {
            case USER_FIRST_NAME:
                updateUserFirstName(answer);
                break;
        }
    }

    private void updateUserFirstName(Answer answer) {
        User user = persisted.getCurrentUser();
        if (user == null)
            return;

        String firstName = answer.getValue();
        if (TextUtils.isEmpty(firstName))
            return;

        user.setFirstName(firstName);
        persisted.setCurrentUser(user);
        userManager.updateUserFirstName(user.getId(), user.getFirstName(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe();
    }
}
