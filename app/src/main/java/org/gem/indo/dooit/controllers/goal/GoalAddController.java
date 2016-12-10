package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;
import android.net.Uri;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
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

    private BotAdapter botAdapter;

    public GoalAddController(Activity activity, BotAdapter botAdapter, Goal goal, BaseChallenge challenge, Tip tip) {
        super(activity, BotType.GOAL_ADD, goal, challenge, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        this.botAdapter = botAdapter;
        this.goal = goal;
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case ADD_BADGE:
                addBadge(model);
                break;
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

    private void doCreate(Map<String, Answer> answerLog, final BaseBotModel model,
                          final OnAsyncListener listener) {

        if (answerLog.containsKey("goal_add_ask_goal_gallery")) {
            // Predefined Goal branch
            Answer answer = answerLog.get("goal_add_ask_goal_gallery");
            goal.setPrototype(Long.parseLong(answer.values.getString("prototype")));
            goal.setName(answer.values.getString("name"));
            goal.setLocalImageUri(answer.values.getString("image_url"));
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

        // Find Image
        if (answerLog.containsKey("goal_add_a_camera"))
            goal.setLocalImageUri(answerLog.get("goal_add_a_camera").getValue());
        else if (answerLog.containsKey("goal_add_a_gallery"))
            goal.setLocalImageUri(answerLog.get("goal_add_a_gallery").getValue());

        // Upload image if set
        if (goal.hasLocalImageUri()) {
            Uri uri = Uri.parse(goal.getLocalImageUri());
            final String mimetype = context.getContentResolver().getType(uri);
            final String path = MediaUriHelper.getPath(context, uri);
            observe.subscribe(new Action1<Goal>() {
                @Override
                public void call(final Goal newGoal) {
                    uploadImage(newGoal, mimetype, new File(path));
                }
            });
        } else {
            observe.subscribe();
        }

        // Persist goal so that when user leaves conversation and returns, the view holders can
        // display their previous data properly.
        persisted.saveConvoGoal(botType, goal);
    }

    private void uploadImage(Goal goal, String mimetype, File file) {
        fileUploadManager.uploadGoalImage(goal.getId(), mimetype, file, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe();
    }

    private void addBadge(BaseBotModel model) {
        if (goal.hasNewBadges()) {
            //Badge badge = goal.getNewBadges().get(0);
            Node node = new Node();
            node.setName(model.getName() + "_badge");
            node.setType(BotMessageType.BADGE);
            botAdapter.addItem(node);
        }
    }
}
