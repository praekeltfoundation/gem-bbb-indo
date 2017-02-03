package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.android.gms.analytics.AnalyticsService;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.images.MediaUriHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.MainActivity;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/12/01.
 */

public class GoalEditController extends GoalBotController {

    @Inject
    transient GoalManager goalManager;

    @Inject
    transient FileUploadManager fileUploadManager;

    Goal oldGoal;

    public GoalEditController(Activity activity, BotRunner botRunner, Goal goal, BaseChallenge challenge, Tip tip, Goal oldGoal) {
        super(activity, botRunner, BotType.GOAL_EDIT, goal, challenge, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        this.goal = goal;
        if(oldGoal == null){
            this.oldGoal = goal.copy();
        }else{
            this.oldGoal = oldGoal;
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case UPDATE_LOCAL_GOAL:
                doLocalUpdate(answerLog);
                break;
            case UPDATE_GOAL_CONFIRM:
                confirmLocalUpdate(answerLog);
                break;
        }
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case DO_UPDATE:
                doUpdate(answerLog, listener);
                break;
            case DO_DELETE:
                doDelete(answerLog, listener);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public boolean filter(Answer answer) {
        switch (answer.getName()) {
            case "goal_edit_delete_tip":
            case "goal_edit_info_tip":
                return tip != null;
            case "goal_edit_delete_challenge":
            case "goal_edit_info_challenge":
                return challenge != null && challenge.isActive();
            default:
                return true;
        }
    }

    private void doLocalUpdate(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("goal_edit_choice_date"))
            goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDate(answerLog.get("goal_end_date").getValue().substring(0, 10)));
        else if (answerLog.containsKey("goal_target"))
            goal.setTarget(Double.parseDouble(answerLog.get("goal_target").getValue()));
        else if (answerLog.containsKey("goal_edit_gallery") || answerLog.containsKey("goal_edit_camera")) {
            //Note: This case will have to be handled by and async call
            //handleImage(answerLog, listener);
            // Don't upload Goal
            return;
        }else if(answerLog.containsKey("goal_weekly_target")){
            goal.setWeeklyTarget(Double.parseDouble(answerLog.get("goal_weekly_target").getValue()));
        }

        persisted.saveConvoGoal(botType, goal);
        persisted.saveOldConvoGoal(botType, oldGoal);
    }

    private void confirmLocalUpdate(Map<String, Answer> answerLog) {
        if (    answerLog.containsKey("goal_edit_target_cancel") ||
                answerLog.containsKey("goal_edit_weekly_target_cancel")
                ){
            oldGoal = goal.copy();
        }else{
            goal = oldGoal;
            oldGoal = goal.copy();
        }


        persisted.saveConvoGoal(botType, goal);
        persisted.saveOldConvoGoal(botType, oldGoal);
    }

    private void doUpdate(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        if (answerLog.containsKey("goal_edit_choice_date"))
            goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDate(answerLog.get("goal_end_date").getValue().substring(0, 10)));
        else if (answerLog.containsKey("goal_edit_target_accept"))
            goal.setTarget(Double.parseDouble(answerLog.get("goal_target").getValue()));
        else if (answerLog.containsKey("goal_edit_gallery") || answerLog.containsKey("goal_edit_camera")) {
            handleImage(answerLog, listener);
            // Don't upload Goal
            return;
        }else if(answerLog.containsKey("goal_edit_weekly_target_accept")){
            goal.setWeeklyTarget(Double.parseDouble(answerLog.get("goal_weekly_target").getValue()));
        }

        goalManager.updateGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        }).subscribe();

        persisted.saveConvoGoal(botType, goal);
    }

    private void handleImage(Map<String, Answer> answerLog, OnAsyncListener listener) {
        // Find Image
        if (answerLog.containsKey("goal_edit_gallery"))
            goal.setLocalImageUri(answerLog.get("goal_edit_gallery").getValue());
        else if (answerLog.containsKey("goal_edit_camera"))
            goal.setLocalImageUri(answerLog.get("goal_edit_camera").getValue());

        Uri imageUri = Uri.parse(goal.getLocalImageUri());

        final String mimetype = context.getContentResolver().getType(imageUri);

        // Crashlytics for MediaURI null check
        try {
            final String path = MediaUriHelper.getPath(context, imageUri);
            uploadImage(goal, mimetype, new File(path), listener);
            CrashlyticsHelper.log(this.getClass().getSimpleName(), "handleImage (BOT): ",
                    "MediaURI.getPath : context: " + context + " uri: " + imageUri);
        } catch (NullPointerException nullException) {
            CrashlyticsHelper.logException(nullException);
        }
    }

    private void uploadImage(final Goal goal, String mimetype, File file, final OnAsyncListener listener) {
        fileUploadManager.uploadGoalImage(goal.getId(), mimetype, file, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                // Clear cache on success
                ImagePipeline pipeline = Fresco.getImagePipeline();
                Uri currentUri = Uri.parse(goal.getImageUrl());
                pipeline.evictFromCache(currentUri);
            }
        });

    }

    private void doDelete(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        goalManager.deleteGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        }).subscribe();
    }
}
