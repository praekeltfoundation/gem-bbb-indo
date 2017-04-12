package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.dao.budget.ExpenseDAO;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.bot.param.ParamArg;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/29.
 */

public class BudgetEditController extends BudgetBotController {

    private static final String TAG = BudgetEditController.class.getName();
    private static String SAVING_DEFAULT_ACCEPT = "budget_edit_a_savings_default_accept";
    private static String EXPENSE_QUESTION_PREFIX = "budget_edit_q_expense_value_";
    private static String EXPENSE_ANSWER_PREFIX = "budget_edit_a_expense_value_";
    private static String EXPENSE_STOP = "budget_create_q_expense_stop";

    private List<Badge> newBadges = null;

    protected ExpenseCategory expenseCategory = null;

    @Inject
    BudgetManager budgetManager;

    public BudgetEditController(Activity activity, BotRunner botRunner, @NonNull Budget budget) {
        super(activity, BotType.BUDGET_EDIT, activity, botRunner);
        ((DooitApplication) activity.getApplication()).component.inject(this);

        this.budget = budget;
    }

    @Override
    public boolean filterQuickAnswer(Answer answer) {
        if (answer.getName().equals("budget_edit_a_expense_option_edit")
                || answer.getName().equals("budget_edit_a_expense_option_replace")) {
            // User can't edit expenses when there are none
            return budget.hasExpenses();
        } else {
            return super.filterQuickAnswer(answer);
        }
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String key = paramType.getKey();
        Expense expense = persisted.loadExpenseToEdit(botType);

        if (model.getName().equals("budget_edit_add_expense_summary")) {
            // Adding new expenses
            switch (paramType) {
                case BUDGET_TOTAL_EXPENSES:
                    if (budget != null) {
                        model.values.put(key, CurrencyHelper.format(budget.getExpenseTotal() + totalEnteredExpenses(botRunner.getAnswerLog())));
                    }
                    break;
                case BUDGET_TOTAL_EXPENSES_REMAINDER:
                    if (budget != null) {
                        model.values.put(key, CurrencyHelper.format(budget.getLeftOver() - totalEnteredExpenses(botRunner.getAnswerLog())));
                    }
                    break;
            }
        } else if (model.getName().equals("budget_edit_replace_add_expense_summary")) {
            switch (paramType) {
                case BUDGET_TOTAL_EXPENSES:
                    if (budget != null)
                        model.values.put(key, CurrencyHelper.format(totalEnteredExpenses(botRunner.getAnswerLog())));
                    break;
                case BUDGET_TOTAL_EXPENSES_REMAINDER:
                    if (budget != null)
                        model.values.put(key, CurrencyHelper.format(budget.getIncome() - budget.getSavings() - totalEnteredExpenses(botRunner.getAnswerLog())));
                    break;
            }
        } else {
            switch (paramType) {
                case BUDGET_DEFAULT_SAVING_PERCENT:
                    model.values.put(key, Budget.DEFAULT_SAVING_PERCENT);
                    break;
                case BUDGET_INCOME:
                    if (budget != null)
                        model.values.put(key, CurrencyHelper.format(budget.getIncome()));
                    break;
                case BUDGET_SAVINGS:
                    if (budget != null)
                        model.values.put(key, CurrencyHelper.format(budget.getSavings()));
                    break;
                case BUDGET_EXPENSE:
                    if (budget != null)
                        model.values.put(key, CurrencyHelper.format(budget.getExpenseTotal()));
                    break;
                case BUDGET_EXPENSE_NAME:
                    if (budget != null && expense != null) {
                        model.values.put(key, expense.getName());
                    }
                    break;
                case BUDGET_EXPENSE_VALUE:
                    if (budget != null && expense != null) {
                        model.values.put(key, expense.getValue());
                    }
                    break;
                case BUDGET_DEFAULT_SAVINGS:
                    if (budget != null)
                        model.values.put(key, CurrencyHelper.format(budget.getDefaultSavings()));
                    break;
                case BUDGET_NEXT_EXPENSE_NAME:
                    if (expenseCategory != null) {
                        model.values.put(key, expenseCategory.getName());
                    }
                    break;
                case BUDGET_TOTAL_EXPENSES:
                    if (expenseCategory != null && expense != null) {
                        model.values.put(key, CurrencyHelper.format(totalExpensesAddSingle()));
                    }
                    break;
                case BUDGET_TOTAL_EXPENSES_REMAINDER:
                    if (expenseCategory != null && expense != null) {
                        model.values.put(key, CurrencyHelper.format(
                                budget.getIncome() - budget.getSavings() - totalExpensesAddSingle()));
                    }
                    break;
                default:
                    super.resolveParam(model, paramType);
            }
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case ADD_EXPENSE:
                addNextExpense(EXPENSE_QUESTION_PREFIX, EXPENSE_ANSWER_PREFIX,
                        model.getName(), // Loop back to this model
                        EXPENSE_STOP,
                        model.getName() + "_summary");
                break;
            case LIST_EXPENSE_QUICK_ANSWERS:
                listExpenseQuickAnswers();
                break;
            case LIST_EXPENSE_CATEGORIES_UNSELECTED:
                listExpenseCategoriesUnselected();
                break;
            case VALIDATE_BUDGET_SAVINGS:
                validateSavingsAmount(answerLog, model);
                break;
            case CLEAR_EXPENSES_STATE:
                clearExpenseState();
                break;
            case CLEAR_AND_FILTER_EXPENSES_STATE:
                clearAndFilterExpenseState();
                break;
            case ADD_BADGE:
                doAddBadge(newBadges);
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case UPDATE_BUDGET_INCOME:
                updateIncome(answerLog, listener);
                break;
            case UPDATE_BUDGET_SAVINGS:
                updateSavings(answerLog, listener);
                break;
            case UPDATE_SINGLE_EXPENSE:
                updateCurrentExpense(answerLog, listener);
                break;
            case UPDATE_BUDGET_SINGLE_EXPENSE:
                addAndUploadSingleExpense(answerLog, listener);
                break;
            case UPLOAD_NEW_EXPENSES:
                uploadNewExpenses(answerLog, listener);
                break;
            case UPLOAD_EXPENSE_REPLACEMENTS:
                uploadExpenseReplacements(answerLog, listener);
                break;
            case DELETE_BUDGET_EXPENSE:
                deleteExpense(answerLog, listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
        }
    }

    @Override
    public void onAnswer(Answer answer) {
        if ("budget_edit_q_user_expenses".equals(answer.getParentName())) {
            long id = Long.parseLong(answer.getValue());
            for (Expense e : budget.getExpenses()) {
                if (e.getId() == id) {
                    persisted.saveExpenseToEdit(botType, e);
                    break;
                }
            }
        } else if ("budget_edit_q_unselected_expenses".equals(answer.getParentName())) {
            long id = Long.parseLong(answer.getValue());
            expenseCategory = ExpenseCategoryBotDAO.findById(botType, id);
        } else if ("single_expense_amount".equals(answer.getName()) && answer.hasValue()) {
            persisted.saveExpenseToEdit(botType, new Expense(expenseCategory, Double.parseDouble(answer.getValue())));
        } else {
            super.onAnswer(answer);
        }
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        switch (inputType) {
            case BUDGET_EXPENSE:
                setExpenseEnteredOnAnswer(answer.getName(), answer.getValue(), EXPENSE_ANSWER_PREFIX);
                break;
        }
    }

    private void updateIncome(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        if (answerLog.containsKey(INCOME_INPUT)) {
            try {
                double income = Double.parseDouble(answerLog.get(INCOME_INPUT).getValue());
                budgetManager.updateBudgetIncome(budget.getId(), income, new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {

                    }
                }).doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        notifyDone(listener);
                    }
                }).subscribe(new Action1<BudgetCreateResponse>() {
                    @Override
                    public void call(BudgetCreateResponse budget) {
                        new BudgetDAO().update(budget.getBudget());
                        BudgetEditController.this.budget = budget.getBudget();
                        newBadges = budget.getBadges();
                    }
                });
            } catch (NumberFormatException e) {
                CrashlyticsHelper.log(TAG, "updateIncome",
                        "Could not parse income from conversation");
                CrashlyticsHelper.logException(e);
            }
        } else {
            Context context = getContext();
            if (context != null)
                Toast.makeText(context, R.string.budget_edit_err_income__not_found, Toast.LENGTH_SHORT).show();
            notifyDone(listener);
        }
    }

    private void updateSavings(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        if (answerLog.containsKey(SAVINGS_INPUT) || answerLog.containsKey(SAVING_DEFAULT_ACCEPT)) {
            try {
                double savings = answerLog.containsKey(SAVING_DEFAULT_ACCEPT)
                        ? budget.getDefaultSavings()
                        : Double.parseDouble(answerLog.get(SAVINGS_INPUT).getValue());
                budgetManager.updateBudgetSavings(budget.getId(), savings, new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {

                    }
                }).doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        notifyDone(listener);
                    }
                }).subscribe(new Action1<BudgetCreateResponse>() {
                    @Override
                    public void call(BudgetCreateResponse budget) {
                        new BudgetDAO().update(budget.getBudget());
                        BudgetEditController.this.budget = budget.getBudget();
                        newBadges = budget.getBadges();
                    }
                });
            } catch (NumberFormatException e) {
                CrashlyticsHelper.log(TAG, "updateSavings",
                        "Could not parse savings from conversation");
                CrashlyticsHelper.logException(e);
            }
        } else {
            Context context = getContext();
            if (context != null)
                Toast.makeText(context, R.string.budget_edit_err_savings__not_found, Toast.LENGTH_SHORT).show();
            notifyDone(listener);
        }
    }

    private void updateCurrentExpense(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        if (answerLog.containsKey("expense_amount")) {
            try {
                Expense expense = persisted.loadExpenseToEdit(botType);
                double expenseAmount = Double.parseDouble(answerLog.get("expense_amount").getValue());
                budget.updateExpense(expense.getId(), expenseAmount);
                budgetManager.upsertBudget(budget, new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {

                    }
                }).doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        notifyDone(listener);
                    }
                }).subscribe(new Action1<BudgetCreateResponse>() {
                    @Override
                    public void call(BudgetCreateResponse budgetCreateResponse) {
                        new BudgetDAO().update(budget);
                        BudgetEditController.this.budget = budget;
                        newBadges = budgetCreateResponse.getBadges();
                    }
                });

            } catch (NumberFormatException e) {
                CrashlyticsHelper.log(TAG, "updateCurrentExpense",
                        "Could not parse expense amount from conversation");
                CrashlyticsHelper.logException(e);
            }
        } else {
            Context context = getContext();
            if (context != null)
                Toast.makeText(context, R.string.budget_edit_err_expense__not_found, Toast.LENGTH_SHORT).show();
            notifyDone(listener);
        }
    }

    private void doAddBadge(List<Badge> badges) {
        if (badges != null && !badges.isEmpty()) {
            persisted.saveNewBudgetBadges(badges);
            for (Badge badge : badges)
                botRunner.queueNode(nodeFromBadge(badge));
        } else {
            Node node = new Node();
            node.setName("save_Conversation_Node");
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext("budget_edit_q_final_positive");
            botRunner.queueNode(node);
        }
    }

    private Node nodeFromBadge(Badge badge) {
        // TODO: Think of a unified way to construct Nodes programmatically. Should it be done in the view holders? Factories?

        String badgeName = botType.name().toLowerCase() + "_" + badge.getGraphName();

        // Badge Graphic Display
        Node node = new Node();
        node.setName(badgeName);
        node.setType(BotMessageType.BADGE);
        node.setText(null);
        node.setAutoNext("budget_edit_a_yay");

        node.values.put(BotParamType.BADGE_NAME.getKey(), badge.getName());
        node.values.put(BotParamType.BADGE_IMAGE_URL.getKey(), badge.getImageUrl());
        node.values.put(BotParamType.BADGE_SOCIAL_URL.getKey(), badge.getSocialUrl());
        node.finish();

        if (badge.hasIntro()) {
            // Badge Intro Text
            Node introNode = new Node();
            introNode.setName(badgeName + "_intro");
            introNode.setType(BotMessageType.TEXT);
            introNode.setAutoNext(node);

            // TODO: Refactor Param parsing and populating into DooitBotController
            // TODO: Text is processed here because Nodes currently don't support having text sourced from somewhere that's not the strings.xml files
            ParamMatch args = ParamParser.parse(badge.getIntro());
            if (!args.isEmpty())
                for (ParamArg arg : args.getArgs())
                    resolveParam(introNode, BotParamType.byKey(arg.getKey()));
            introNode.setProcessedText(args.process(introNode.values.getRawMap()));

            return introNode;
        } else {
            return node;
        }
    }

    /**
     * Notifies the user that their savings amount isn't valid by adding an error node to the
     * conversation.
     *
     * @param answerLog
     * @param model
     */
    private void validateSavingsAmount(Map<String, Answer> answerLog, BaseBotModel model) {
        Answer answer = answerLog.get("savings_amount");
        if (answer != null) {
            try {
                double amount = Double.parseDouble(answer.getValue());

                Node node = new Node();
                node.setName("budget_edit_savings_amount_intermediate");
                node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload

                if (budget.getIncome() >= amount) {
                    //isValid
                    node.setAutoNext("budget_edit_update_savings");
                } else {
                    //isNotValid
                    node.setAutoNext("budget_edit_savings_amount_invalid");
                }
                node.finish();
                botRunner.queueNode(node);
            } catch (NumberFormatException e) {
                CrashlyticsHelper.log(TAG, "validateSavingsAmount",
                        "Could not amount from conversation");
                CrashlyticsHelper.logException(e);

                //make sure the conversation continues
                Node node = new Node();
                node.setName("budget_edit_savings_amount_intermediate");
                node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload
                node.setAutoNext("budget_edit_savings_amount_invalid");
                node.finish();
                botRunner.queueNode(node);
            }
        }
    }

    private void clearExpenseState() {
        ExpenseCategoryBotDAO.cleatState(botType);
    }

    /**
     * Clears the state of expense categories in preparation for the carousel.
     */
    private void clearAndFilterExpenseState() {
        if (budget == null)
            return;

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            RealmResults<ExpenseCategory> categories = realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .findAll();

            // Keep a set of the budget expense ids so we can know which categories have been added
            // before.
            HashSet<Long> existing = new HashSet<>();
            for (Expense expense : budget.getExpenses())
                if (expense.hasCategoryId())
                    existing.add(expense.getCategoryId());

            realm.beginTransaction();
            for (ExpenseCategory category : categories) {
                category.setSelected(false);
                category.setEntered(false);
                category.setEnabled(!existing.contains(category.getId()));
            }
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void addAndUploadSingleExpense(@NonNull Map<String, Answer> answerLog,
                                           @NonNull final OnAsyncListener listener) {
        budget.addExpense(persisted.loadExpenseToEdit(botType));
        doUpload(answerLog, listener);
    }

    private void uploadNewExpenses(@NonNull Map<String, Answer> answerLog,
                                   @NonNull final OnAsyncListener listener) {
        if (budget == null) {
            notifyDone(listener);
            return;
        }

        for (ExpenseCategory category : ExpenseCategoryBotDAO.findEntered(botType)) {
            String key = EXPENSE_ANSWER_PREFIX + Long.toString(category.getId());
            double expense = 0.0;
            if (answerLog.containsKey(key))
                expense = Double.parseDouble(answerLog.get(key).getValue());

            budget.addExpense(new Expense(category, expense));
        }
        new BudgetDAO().update(budget);

        doUpload(answerLog, listener);
    }

    private void uploadExpenseReplacements(@NonNull Map<String, Answer> answerLog,
                                           @NonNull final OnAsyncListener listener) {

        // Clear all existing expenses from budget
        if (budget == null) {
            notifyDone(listener);
            return;
        }
        budget = BudgetDAO.clearExpenses(budget.getId());
        if (budget == null) {
            notifyDone(listener);
            // Update failed
            return;
        }

        // Add new expenses to budget
        for (ExpenseCategory category : ExpenseCategoryBotDAO.findEntered(botType)) {
            String key = EXPENSE_ANSWER_PREFIX + Long.toString(category.getId());
            double expense = 0.0;
            if (answerLog.containsKey(key))
                expense = Double.parseDouble(answerLog.get(key).getValue());

            budget.addExpense(new Expense(category, expense));
        }

        new BudgetDAO().update(budget);

        doUpload(answerLog, listener);
    }

    private void doUpload(@NonNull Map<String, Answer> answerLog,
                          @NonNull final OnAsyncListener listener) {

        // Upload Budget to server
        budgetManager.upsertBudget(budget, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                notifyDone(listener);
            }
        }).subscribe(new Action1<BudgetCreateResponse>() {
            @Override
            public void call(BudgetCreateResponse response) {
                // Budget Primary Key is from server
                new BudgetDAO().update(response.getBudget());

                // Store a reference to the Budget as it was received
                BudgetEditController.this.budget = response.getBudget();

                // TODO: Badges from budget create
//                persisted.saveNewBudgetBadges(response.getBadges());
            }
        });
    }

    private double totalExpensesAddSingle() {
        double total = 0;
        for (Expense e : budget.getExpenses()) {
            if (e.getCategoryId() != expenseCategory.getId()) {
                total += e.getValue();
            }
        }

        Expense expense = persisted.loadExpenseToEdit(botType);
        if (expense != null)
            total += expense.getValue();

        return total;
    }

    /**
     * Sums the total of the selected and entered expenses.
     */
    private double totalEnteredExpenses(Map<String, Answer> answerLog) {
        double expense = 0.0;
        List<ExpenseCategory> categories = ExpenseCategoryBotDAO.findEntered(botType);
        try {
            for (ExpenseCategory category : categories) {
                if (answerLog.containsKey(EXPENSE_ANSWER_PREFIX + Long.toString(category.getId()))) {
                    expense += Double.parseDouble(answerLog.get(EXPENSE_ANSWER_PREFIX + Long.toString(category.getId())).getValue());
                }
            }
        } catch (NumberFormatException e) {
            CrashlyticsHelper.logException(e);
        }
        return expense;
    }

    /**
     * Builds a dummy node which lists all of the user's current expenses, to be edited.
     */
    private void listExpenseQuickAnswers() {
        Node node = new Node();
        node.setName("budget_edit_q_user_expenses");
        node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload

        for (Expense expense : budget.getExpenses()) {
            Answer answer = new Answer();
            answer.setName("budget_edit_a_user_expense_" + Long.toString(expense.getId()));
            answer.setProcessedText(String.format(Locale.US, "%s - %s",
                    expense.getName(), CurrencyHelper.format(expense.getValue())));
            answer.setValue(Long.toString(expense.getId()));
            answer.setNext("budget_edit_q_expense_option_edit_selected");

            node.addAnswer(answer);
        }

        node.finish();

        botRunner.queueNode(node);
    }

    /**
     * Builds a dummy node which lists all expense categories not yet added.
     */
    private void listExpenseCategoriesUnselected() {
        ExpenseCategoryBotDAO categoryDAO = new ExpenseCategoryBotDAO();
        for (Expense e : budget.getExpenses()) {
            long catId = e.getCategoryId();
            if (catId >= 0) {
                categoryDAO.setSelectedByRemoteId(catId, true);
            }
        }
        List<ExpenseCategory> expenseCategories = new ExpenseCategoryBotDAO().findUnselected(botType);
        Node node = new Node();
        node.setName("budget_edit_q_unselected_expenses");
        node.setType(BotMessageType.DUMMY); // Keep the node in the conversation on reload

        for (ExpenseCategory category : expenseCategories) {
            Answer answer = new Answer();
            answer.setName("budget_edit_a_unselected_expenses_" + Long.toString(category.getId()));
            answer.setProcessedText(String.format(Locale.US, "%s",
                    category.getName()));
            answer.setValue(Long.toString(category.getId()));
            answer.setNext("budget_edit_q_expense_option_add_selected");

            node.addAnswer(answer);
        }

        node.finish();

        botRunner.queueNode(node);
    }

    private void deleteExpense(Map<String, Answer> answerLog, final OnAsyncListener listener) {
        final Expense expense = persisted.loadExpenseToEdit(botType);
        if (budget != null && expense != null) {
            budgetManager.deleteExpense(expense.getId(), new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {
                }
            }).doOnTerminate(new Action0() {
                @Override
                public void call() {
                    notifyDone(listener);
                }
            }).subscribe(new Action1<EmptyResponse>() {
                @Override
                public void call(EmptyResponse emptyResponse) {
                    ExpenseDAO.delete(expense.getId());
                    budget.removeExpense(expense.getId());
                }
            });
        }
    }

    ////////////////
    // Validation //
    ////////////////

    @Override
    public boolean validate(String name, String input) {
        if (name.startsWith(EXPENSE_ANSWER_PREFIX))
            return validateExpense(input);
        return super.validate(name, input);
    }
}
