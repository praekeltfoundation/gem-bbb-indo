package org.gem.indo.dooit.views.main.fragments.target.controllers;

import android.app.Activity;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
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

    public GoalEditController(Activity activity, Goal goal, Tip tip) {
        super(activity, BotType.GOAL_EDIT, goal, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        this.goal = goal;
    }

    @Override
    public void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case "do_update":
                doUpdate(answerLog);
                break;
            case "do_delete":
                doDelete(answerLog);
                break;
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public Object getObject() {
        return goal;
    }

    private void doUpdate(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("goal_edit_choice_date"))
            goal.setEndDate(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDate(answerLog.get("goal_end_date").getValue().substring(0, 10)));
        else if (answerLog.containsKey("goal_edit_target_accept"))
            goal.setTarget(Double.parseDouble(answerLog.get("goal_target").getValue()));
        else if (answerLog.containsKey("goal_edit_gallery") || answerLog.containsKey("goal_edit_camera")) {
            handleImage(answerLog);
            // Don't upload Goal
            return;
        }

        goalManager.updateGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        }).subscribe();

        persisted.saveConvoGoal(botType, goal);
    }

    private void handleImage(Map<String, Answer> answerLog) {
        // Find Image
        Uri imageUri = null;
        if (answerLog.containsKey("goal_edit_gallery"))
            imageUri = Uri.parse(answerLog.get("goal_edit_gallery").getValue());
        else if (answerLog.containsKey("goal_edit_camera"))
            imageUri = Uri.parse(answerLog.get("goal_edit_camera").getValue());

        final String mimetype = context.getContentResolver().getType(imageUri);
        final String path = MediaUriHelper.getPath(context, imageUri);
        uploadImage(goal, mimetype, new File(path));
    }

    private void uploadImage(final Goal goal, String mimetype, File file) {
        fileUploadManager.uploadGoalImage(goal.getId(), mimetype, file, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
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

    private void doDelete(Map<String, Answer> answerLog) {
        goalManager.deleteGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                if (context instanceof MainActivity)
                    ((MainActivity) context).refreshGoals();
            }
        }).subscribe();
    }
}
