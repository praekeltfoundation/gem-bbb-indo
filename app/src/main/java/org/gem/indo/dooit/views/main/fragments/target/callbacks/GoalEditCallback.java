package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.BotCallback;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/12/01.
 */

public class GoalEditCallback implements BotCallback {

    @Inject
    transient GoalManager goalManager;

    private Goal goal;

    public GoalEditCallback(DooitApplication application, Goal goal) {
        application.component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case "do_update":
                doUpdate(answerLog);
                break;
        }
    }

    private void doUpdate(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("goal_edit_choice_date"))
            goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDate(answerLog.get("goal_end_date").getValue().substring(0, 10)));
        else if (answerLog.containsKey("goal_edit_target_accept"))
            goal.setTarget(Double.parseDouble(answerLog.get("goal_target").getValue()));

        goalManager.updateGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe();
    }
}
