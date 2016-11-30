package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import android.util.Log;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BotCallback;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public class GoalAddCallback implements BotCallback {

    private static final String TAG = GoalAddCallback.class.getName();

    @Inject
    transient GoalManager goalManager;

    public GoalAddCallback(DooitApplication application) {
        application.component.inject(this);
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {
        Goal goal = new Goal();
        if (answerLog.containsKey("goal_add_carousel_answer")) {
            // Predefined Goal
            Answer answer = answerLog.get("goal_add_carousel_answer");
            goal.setPrototype(Long.parseLong(answer.get("prototype")));
            goal.setName(answer.get("name"));
            goal.setImageUrl(answer.get("image_url"));
        } else {
            // Custom Goal
            goal.setName(answerLog.get("goal_name").getValue());
        }
        goal.setTarget(Float.parseFloat(answerLog.get("goal_amount").getValue()));
        goal.setStartDate(LocalDate.now());
        goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                .parseLocalDate(answerLog.get("goalDate").getValue().substring(0, 10)));

        if (answerLog.containsKey("hasSavedY"))
            goal.createTransaction(Double.parseDouble(answerLog.get("priorSaveAmount").getValue()));

        Log.d(TAG, "Created " + goal);

        goalManager.createGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<Goal>() {
            @Override
            public void call(Goal goal) {
                Log.d(TAG, goal + " successfully created");
            }
        });
    }
}
