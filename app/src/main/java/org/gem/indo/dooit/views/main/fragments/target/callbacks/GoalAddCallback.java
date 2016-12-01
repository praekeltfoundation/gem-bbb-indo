package org.gem.indo.dooit.views.main.fragments.target.callbacks;

import android.net.Uri;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.BotCallback;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/20.
 */

public class GoalAddCallback implements BotCallback {

    private static final String TAG = GoalAddCallback.class.getName();

    @Inject
    transient GoalManager goalManager;

    @Inject
    transient FileUploadManager fileUploadManager;

    private transient MainFragment fragment;

    public GoalAddCallback(DooitApplication application, MainFragment fragment) {
        application.component.inject(this);
        this.fragment = fragment;
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {
        Goal goal = new Goal();

        if (answerLog.containsKey("goal_add_ask_goal_gallery")) {
            // Predefined Goal branch
            Answer answer = answerLog.get("goal_add_ask_goal_gallery");
            goal.setPrototype(Long.parseLong(answer.get("prototype")));
            goal.setName(answer.get("name"));
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

        // Ready Goal request
        Observable<Goal> observe = goalManager.createGoal(goal, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });

        // Find Image
        Uri imageUri = null;
        if (answerLog.containsKey("Capture"))
            imageUri = Uri.parse(answerLog.get("Capture").getValue());
        else if (answerLog.containsKey("Gallery"))
            imageUri = Uri.parse(answerLog.get("Gallery").getValue());

        // Upload image if set
        if (imageUri != null){
            final String mimetype = fragment.getContext().getContentResolver().getType(imageUri);
            final String path = MediaUriHelper.getPath(fragment.getContext(), imageUri);
            observe.subscribe(new Action1<Goal>() {
                @Override
                public void call(Goal goal) {
                    uploadImage(goal, mimetype, new File(path));
                }
            });
        } else {
            observe.subscribe();
        }
    }

    private void uploadImage(Goal goal, String mimetype, File file) {
        fileUploadManager.uploadGoalImage(goal.getId(), mimetype, file, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe();
    }

    @Override
    public void onCall(String key, Map<String, Answer> answerLog, BaseBotModel model) {

    }
}
