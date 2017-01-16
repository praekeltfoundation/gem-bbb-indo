package org.gem.indo.dooit.helpers;

import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.goal.Goal;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Helper to build a map of parameters which can be used when processing a
 * {@link org.gem.indo.dooit.helpers.bot.param.ParamMatch}.
 *
 * The params are application specific.
 *
 * Created by Wimpie Victor on 2017/01/16.
 */

public class DooitParamBuilder {

    @Inject
    Persisted persisted;

    private User user;
    private Goal goal;

    private DooitParamBuilder(Context context) {
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
    }

    public DooitParamBuilder setGoal(Goal goal) {
        this.goal = goal;
        return this;
    }

    public Map<String, String> build() {
        Map<String, String> map = new HashMap<>();

        if (user != null) {
            map.put(BotParamType.USER_USERNAME.getKey(), user.getUsername());
            map.put(BotParamType.USER_FIRST_NAME.getKey(), user.getFirstName());
            map.put(BotParamType.USER_PREFERRED_NAME.getKey(), user.getPreferredName());
        }

        return new HashMap<>();
    }
}
