package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;
import android.net.Uri;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.images.MediaUriHelper;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.MainActivity;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public class GoalAddController extends GoalBotController {

    private static final String TAG = GoalAddController.class.getName();

    @Inject
    GoalManager goalManager;

    @Inject
    FileUploadManager fileUploadManager;

    @Inject
    Persisted persisted;

    public GoalAddController(Activity activity, BotRunner botRunner, Goal goal, BaseChallenge challenge, Tip tip) {
        super(activity, botRunner, BotType.GOAL_ADD, goal, challenge, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        if (this.goal == null)
            this.goal = new Goal();
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case POPULATE_GOAL:
                doPopulate(answerLog);
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model,
                            OnAsyncListener listener) {
        switch (key) {
            case DO_CREATE:
                doCreate(answerLog, model, listener);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    private void doPopulate(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("goal_add_ask_goal_gallery")) {
            // Predefined Goal branch
            Answer answer = answerLog.get("goal_add_ask_goal_gallery");

            goal.setPrototype(answer.values.getLong(BotParamType.GOAL_PROTO_ID.getKey()));
            goal.setName(answer.values.getString(BotParamType.GOAL_PROTO_NAME.getKey()));
            goal.setLocalImageUri(answer.values.getString(BotParamType.GOAL_PROTO_IMAGE_URL.getKey()));
            goal.setImageFromProto(true);
        } else {
            // Custom Goal branch
            goal.setName(answerLog.get("goal_name").getValue());
        }

        goal.setTarget(Float.parseFloat(answerLog.get("goal_amount").getValue()));
        goal.setStartDate(LocalDate.now());
        goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                .parseLocalDate(answerLog.get("goalDate").getValue().substring(0, 10)));

        if (answerLog.containsKey("hasSavedY"))
            goal.createTransaction(Double.parseDouble(answerLog.get("priorSaveAmount").getValue()));

        // Find Image
        if (answerLog.containsKey("goal_add_a_camera")) {
            goal.setLocalImageUri(answerLog.get("goal_add_a_camera").getValue());
            goal.setImageFromProto(false);
        } else if (answerLog.containsKey("goal_add_a_gallery")) {
            goal.setLocalImageUri(answerLog.get("goal_add_a_gallery").getValue());
            goal.setImageFromProto(false);
        }

        goal.calculateFields();

        persisted.saveConvoGoal(botType, goal);
    }

    private void doCreate(Map<String, Answer> answerLog, final BaseBotModel model,
                          final OnAsyncListener listener) {
        // Ready Goal request
        Observable<Goal> observe = goalManager.createGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
                // After the user has created their first Goal, they should no longer receive the
                // initial default conversation.
                persisted.setNewBotUser(false);
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        });

        // Upload image if set
        if (goal.hasLocalImageUri() && !goal.imageFromProto()) {
            Uri uri = Uri.parse(goal.getLocalImageUri());
            final String mimetype = context.getContentResolver().getType(uri);
            final String path = MediaUriHelper.getPath(context, uri);
            observe.subscribe(new Action1<Goal>() {
                @Override
                public void call(final Goal newGoal) {
                    // New Achievements is what we care about
                    goal.addNewBadges(newGoal.getNewBadges());
                    saveGoal();
                    uploadImage(newGoal, mimetype, new File(path));
                }
            });
        } else {
            observe.subscribe(new Action1<Goal>() {
                @Override
                public void call(Goal newGoal) {
                    goal.addNewBadges(newGoal.getNewBadges());
                    saveGoal();
                }
            });
        }

        // Persist goal so that when user leaves conversation and returns, the view holders can
        // display their previous data properly.
        saveGoal();
    }

    private void uploadImage(Goal goal, String mimetype, File file) {
        fileUploadManager.uploadGoalImage(goal.getId(), mimetype, file, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        });
    }

    private void saveGoal() {
        persisted.saveConvoGoal(botType, goal);
    }
}
