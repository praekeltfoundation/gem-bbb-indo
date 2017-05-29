package org.gem.indo.dooit.controllers.goal;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
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

    protected Budget budget;

    @Inject
    GoalManager goalManager;

    @Inject
    FileUploadManager fileUploadManager;

    @Inject
    Persisted persisted;

    public GoalAddController(Activity activity, BotRunner botRunner, Budget budget, List<GoalPrototype> prototypes,
                             Goal goal, BaseChallenge challenge, Tip tip) {
        super(activity, botRunner, BotType.GOAL_ADD, prototypes, goal, challenge, tip);
        ((DooitApplication) activity.getApplication()).component.inject(this);
        if (this.goal == null)
            this.goal = new Goal();
        this.budget = budget;
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
            case SHOW_BUDGET_DIAGRAM:
                checkShowBudget();
                break;
            case GOAL_CLEAR_INITIAL:
                clearInitialSavings(answerLog);
                break;
            case GOAL_EARLY_COMPLETION:
                checkGoalEarlyCompletion(answerLog);
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
    public Object getObject(BotObjectType objType) {
        switch (objType) {
            case BUDGET:
                return budget;
            default:
                return super.getObject(objType);
        }
    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        super.onAnswerInput(inputType, answer);

        try {
            // Goal is stored in case the view is destroyed during the conversation
            persisted.saveConvoGoal(botType, goal);
        } catch (IllegalArgumentException e) {
            // Logging for infinite double error
            CrashlyticsHelper.log(TAG, "on Answer Input (addGoal): ", "goal start date: " + goal.getStartDate() +
                    " Target amount: " + goal.getTarget() + " Goal name: " + goal.getName() +
                    " Goal Weekly Target: " + goal.getWeeklyTarget());

            CrashlyticsHelper.logException(e);
        }
    }

    private void doPopulate(Map<String, Answer> answerLog) {
        // Goal Prototype is mandatory, but can still be empty if no prototypes were set up on the
        // server.
        Answer protoAnswer = answerLog.get("goal_add_ask_goal_gallery");

        // Prototype ID
        if (protoAnswer != null) {
            goal.setPrototypeId(protoAnswer.values.getLong(BotParamType.GOAL_PROTO_ID.getKey()));
            for (GoalPrototype proto : prototypes) {
                if (proto.getId() == goal.getPrototypeId()) {
                    goal.setPrototype(proto);
                    goal.setTarget(proto.getDefaultPrice());
                    break;
                }
            }
        }

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
            goal.setWeeklyTarget(Double.parseDouble(answerLog.get("weeklySaveAmount").getValue()));
        }
        CrashlyticsHelper.log(TAG, "do Populate (addGoal): ", "goal start date: " + goal.getStartDate() +
                " Target amount: " + goal.getTarget() + " Goal name: " + goal.getName());

        // User has existing savings
        // Populate can be called multiple times in the conversation. We clear the transactions
        // to avoid creating duplicates. This conversation only creates one transaction.
        goal.clearTransactions();
        if (answerLog.containsKey("hasSavedY")) {
            goal.setInitialAmount(Double.parseDouble(answerLog.get("priorSaveAmount").getValue()));
        }

        // Goal Image
        if (answerLog.containsKey("goal_add_a_goal_image")) {
            goal.setLocalImageUri(answerLog.get("goal_add_a_goal_image").getValue());
            goal.setImageFromProto(false);
        } else if (protoAnswer != null) {
            // Skipped; use Goal Prototype image
            goal.setLocalImageUri(protoAnswer.values.getString(BotParamType.GOAL_PROTO_IMAGE_URL.getKey()));
            goal.setImageFromProto(true);
        }

        try {
            // Goal is stored in case the view is destroyed during the conversation
            persisted.saveConvoGoal(botType, goal);
        } catch (IllegalArgumentException e) {
            // Logging for infinite double error
            CrashlyticsHelper.log(TAG, "do Populate (addGoal): ", "goal start date: " + goal.getStartDate() +
                    " Target amount: " + goal.getTarget() + " Goal name: " + goal.getName() +
                    " Goal Weekly Target: " + goal.getWeeklyTarget());

            CrashlyticsHelper.logException(e);
        }
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
                /*
                TODO: This line is causing an ILLEGALARGUMENTSEXCEPTION in the MediaUriHelper class. It needs investigation on how to solve that issue. Storing the valid image path in the Answer.values map is a temporary solution.
                final String path = MediaUriHelper.getPath(context, uri);
                */
                final Map<String, Answer> answerLogTemp = answerLog;
                observe.subscribe(new Action1<Goal>() {
                    @Override
                    public void call(final Goal newGoal) {
                        // New Achievements is what we care about
                        goal.addNewBadges(newGoal.getNewBadges());
                        saveGoal();

                        //the path is now extracted form the Answer.values map as explained int the comment above "final String path = MediaUriHelper.getPath(context, uri);"
                        String path = null;
                        if(answerLogTemp.containsKey("goal_add_a_goal_image")){
                            Answer imageAnswer = answerLogTemp.get("goal_add_a_goal_image");
                            path = imageAnswer.values.getString(Answer.IMAGEPATH);
                        }else{
                            path = goal.getPrototype().getImageUrl();
                        }

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

    private void checkShowBudget() {
        Node node = new Node();

        if (budget == null) {
            node.setName("goal_add_q_budget_intro_00");
            node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
            node.setAutoNext("goal_add_q_budget_intro_01");
        } else {
            node.setName("goal_add_show_budget_diagram");
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext("budgetDiagram");
        }

        node.finish();
        botRunner.queueNode(node);
    }

    private void clearInitialSavings(Map<String, Answer> answerLog) {
        if (answerLog.containsKey("priorSaveAmount")) {
            Answer a = answerLog.get("priorSaveAmount");
            a.setValue("0");
        }

        goal.setInitialAmount(0);
    }

    private void checkGoalEarlyCompletion(Map<String, Answer> answerLog) {
        doPopulate(answerLog);

        goal.calculateWeeklyTarget();

        Node node = new Node();

        if (goal.willReachGoalEarly() && goal.getEarlyCompleteDays() % 7 == 0) {
            node.setName("goal_add_early_completion_no_remainder_days");
            node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
            node.setAutoNext("goal_add_q_verify_early_completion_no_remainder_days");
        } else if (goal.willReachGoalEarly()) {
            node.setName("goal_add_early_completion");
            node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
            node.setAutoNext("goal_add_q_verify_early_completion");
        } else if (goal.getEarlyCompleteDays() == 0) {
            node.setName("goal_add_rounded_no_early_completion");
            node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
            node.setAutoNext("goal_add_q_verify_rounded_no_early");
        } else {
            node.setName("goal_add_no_early_completion");
            node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
            node.setAutoNext("goal_add_q_verify");
        }

        node.finish();
        botRunner.queueNode(node);
    }

    ////////////////
    // Validation //
    ////////////////

    @Override
    public boolean validate(String name, String input) {
        switch (name) {
            case "goal_add_user_firstname":
            case "goal_name":
                return validateName(input);
            case "goal_amount":
            case "goal_add_q_change_target":
                // Goal Target
                return validateTarget(input);
            case "priorSaveAmount":
                // Existing savings
                return validateTransaction(input);
            case "weeklySaveAmount":
                // Weekly target
                return validateWeeklyTarget(input);
            default:
                return super.validate(name, input);
        }
    }

    private boolean validateName(String input) {
        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(input.trim())) {
            toast(R.string.goal_add_err_user_firstname__empty);
            return false;
        }
        return true;
    }

    private boolean validateTarget(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.goal_add_err_goal_target__empty);
            return false;
        }

        try {
            double value = Double.parseDouble(input);
            if (value <= 0.0) {
                toast(R.string.goal_add_err_goal_target__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.goal_add_err__invalid_number);
            return false;
        }
    }

    private boolean validateTransaction(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.goal_add_err_transaction__empty);
            return false;
        }

        try {
            double value = Double.parseDouble(input);
            if (value <= 0.0) {
                toast(R.string.goal_add_err_transaction__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.goal_add_err__invalid_number);
            return false;
        }
    }

    private boolean validateWeeklyTarget(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.goal_add_err_weekly_target__empty);
            return false;
        }

        try {
            double value = Double.parseDouble(input);

            // Search for Goal Target entry
            Map<String, Answer> answerLog = botRunner.getAnswerLog();
            Double goalTarget = null;
            if (answerLog.containsKey("goal_amount"))
                goalTarget = Double.parseDouble(answerLog.get("goal_amount").getValue());

            if (value <= 0.0) {
                toast(R.string.goal_add_err_weekly_target__zero);
                return false;
            } else if (goalTarget != null && value > goalTarget) {
                toast(R.string.goal_add_err_weekly_target__goal_target);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.goal_add_err__invalid_number);
            return false;
        }
    }
}
