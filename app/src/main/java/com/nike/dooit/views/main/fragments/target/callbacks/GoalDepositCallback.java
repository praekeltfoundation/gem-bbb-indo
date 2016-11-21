package com.nike.dooit.views.main.fragments.target.callbacks;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.models.bot.BotCallback;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/11/21.
 */

public class GoalDepositCallback implements BotCallback {

    @Inject
    transient GoalManager goalManager;

    public GoalDepositCallback(DooitApplication application) {
        application.component.inject(this);
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }
}
