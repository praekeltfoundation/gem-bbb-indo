package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.images.MediaUriHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.views.main.MainActivity;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.List;
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

    public GoalAddController(Activity activity, BotRunner botRunner, List<GoalPrototype> prototypes,
                             Goal goal, BaseChallenge challenge, Tip tip) {
        super(activity, botRunner, BotType.GOAL_ADD, prototypes, goal, challenge, tip);
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
            case SET_TARGET_TO_DEFAULT:
                setTargetAsDefault();
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
    public boolean shouldSkip(BaseBotModel model) {
        return super.shouldSkip(model);
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    private void doPopulate(Map<String, Answer> answerLog) {
        // Goal Prototype is mandatory, but can still be empty if no prototypes were set up on the
        // server.
        Answer protoAnswer = answerLog.get("goal_add_ask_goal_gallery");

        // Prototype ID
        if (protoAnswer != null)
            goal.setPrototypeId(protoAnswer.values.getLong(BotParamType.GOAL_PROTO_ID.getKey()));

        // Goal Name
        String name = answerLog.get("goal_name").getValue();
        if (TextUtils.isEmpty(name))
            if (protoAnswer != null)
                goal.setName(protoAnswer.values.getString(BotParamType.GOAL_PROTO_NAME.getKey()));
            else
                goal.setName("");
        else
            goal.setName(name);

        // Goal Target
        if (answerLog.containsKey("goal_amount"))
            goal.setTarget(Float.parseFloat(answerLog.get("goal_amount").getValue()));

        // Start Date
        goal.setStartDate(LocalDate.now());

        // End date
        if (answerLog.containsKey("goalDate")) {
            CrashlyticsHelper.log(TAG, "doPopulate", "User inputted an end date using the calendar");
            goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDate(answerLog.get("goalDate").values.getString("date")));
        } else if (answerLog.containsKey("weeklySaveAmount")) {
            CrashlyticsHelper.log(TAG, "doPopulate", "User inputted a weekly target");
            double weeklyTarget = Double.parseDouble(answerLog.get("weeklySaveAmount").getValue());
            LocalDate endDate = new LocalDate(Goal.endDateFromTarget(goal.getStartDate().toDate(),
                    goal.getTarget(), weeklyTarget));
            goal.setEndDate(endDate);
        }
        CrashlyticsHelper.log(TAG, "do Populate (addGoal): ", "goal start date: " + goal.getStartDate() +
                " Target amount: " + goal.getTarget() + " Goal name: " + goal.getName());

        // User has existing savings
        if (answerLog.containsKey("hasSavedY"))
            goal.createTransaction(Double.parseDouble(answerLog.get("priorSaveAmount").getValue()));

        // Goal Image
        if (answerLog.containsKey("goal_add_a_camera")) {
            goal.setLocalImageUri(answerLog.get("goal_add_a_camera").getValue());
            goal.setImageFromProto(false);
        } else if (answerLog.containsKey("goal_add_a_gallery")) {
            goal.setLocalImageUri(answerLog.get("goal_add_a_gallery").getValue());
            goal.setImageFromProto(false);
        } else if (protoAnswer != null) {
            // Skipped; use Goal Prototype image
            goal.setLocalImageUri(protoAnswer.values.getString(BotParamType.GOAL_PROTO_IMAGE_URL.getKey()));
            goal.setImageFromProto(true);
        }

        // Goal is stored in case the view is destroyed during the conversation
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

            // Crashlytics for MediaURI null check
            try {
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

                CrashlyticsHelper.log(this.getClass().getSimpleName(), "doCreate (BOT) : ",
                        "MediaURI.getPath : context: " + context + " uri: " + uri + "MimeType: " + mimetype);
            } catch (NullPointerException nullException) {
                CrashlyticsHelper.logException(nullException);
            }

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

    @Override
    public boolean filterQuickAnswer(Answer answer) {
        switch (answer.getName()) {
            case "knows_amount_N":
                return goal.hasPrototype() && (Double.compare(goal.getPrototype().getDefaultPrice(), 0.0) != 0);
            default:
                return true;
        }
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

    private void setTargetAsDefault() {
        if (goal.hasPrototype()) {
            goal.setTarget(goal.getPrototype().getDefaultPrice());
        } else {
            goal.setTarget(0);
        }
    }
}
