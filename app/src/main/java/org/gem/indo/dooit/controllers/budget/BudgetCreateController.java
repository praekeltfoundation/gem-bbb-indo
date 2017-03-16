package org.gem.indo.dooit.controllers.budget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.api.responses.BudgetCreateResponse;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.helpers.bot.BotRunner;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
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
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/01.
 */

public class BudgetCreateController extends DooitBotController {

    private static String TAG = BudgetCreateController.class.getName();

    private static String INCOME = "income_amount";
    private static String SAVING_DEFAULT_ACCEPT = "budget_create_a_savings_default_accept";
    private static String SAVINGS = "savings_amount";
    private static String EXPENSE_QUESTION_PREFIX = "budget_create_q_expense_value_";
    private static String EXPENSE_ANSWER_PREFIX = "budget_create_a_expense_value_";
    private static String EXPENSE_STOP = "budget_create_q_expense_stop";

    @Inject
    BudgetManager budgetManager;

    private MainActivity activity;
    private Budget budget;
    private BotRunner botRunner;

    public BudgetCreateController(Activity activity, @NonNull BotRunner botRunner,
                                  @Nullable Budget budget) {
        super(activity, BotType.BUDGET_CREATE);
        ((DooitApplication) activity.getApplication()).component.inject(this);

        this.activity = (MainActivity) activity;
        this.botRunner = botRunner;

        // A new Budget can be persisted if the conversation is reloaded.
        if (budget == null)
            this.budget = new Budget();
    }

    @Override
    public boolean shouldSkip(BaseBotModel model) {
        if (model.getName().equals("budget_create_add_expense")) {
            return new ExpenseCategoryBotDAO().hasNext(botType);
        }
        return super.shouldSkip(model);
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        switch (inputType) {
            case BUDGET_EXPENSE: {
                String name = answer.getName();
                if (name.startsWith(EXPENSE_ANSWER_PREFIX)) {
                    if (TextUtils.isEmpty(answer.getValue()))
                        // This was the auto answer for the inline answer type, not the text entry
                        return;

                    try {
                        long id = Long.parseLong(name.substring(EXPENSE_ANSWER_PREFIX.length(),
                                name.length()));

                        Realm realm = null;

                        try {
                            realm = Realm.getDefaultInstance();

                            realm.beginTransaction();
                            ExpenseCategory category = realm.where(ExpenseCategory.class)
                                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                                    .equalTo(ExpenseCategory.FIELD_ID, id)
                                    .findFirst();
                            category.setEntered(true);
                            realm.commitTransaction();
                        } catch (Exception e) {
                            CrashlyticsHelper.logException(e);
                            if (realm != null && realm.isInTransaction())
                                realm.cancelTransaction();
                        } finally {
                            if (realm != null)
                                realm.close();
                        }
                    } catch (NumberFormatException e) {
                        CrashlyticsHelper.log(TAG, "onAnswerInput",
                                "Attempt to parse Expense Category ID from answer name failed");
                        CrashlyticsHelper.logException(e);
                    }
                }
                break;
            }
            default:
                super.onAnswerInput(inputType, answer);
        }
    }

    @Override
    public void onAnswer(Answer answer) {
        if (answer.getName().equals("budget_create_a_expense_continue")) {
            // User done with expense carousel. Clear all entered flags
            Realm realm = null;

            try {
                realm = Realm.getDefaultInstance();

                realm.beginTransaction();
                RealmResults<ExpenseCategory> categories = realm.where(ExpenseCategory.class)
                        .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                        .findAll();
                for (ExpenseCategory category : categories)
                    category.setEntered(false);
                realm.commitTransaction();
            } catch (Throwable e) {
                if (realm != null && realm.isInTransaction())
                    realm.close();
            } finally {
                if (realm != null)
                    realm.close();
            }
        }
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        String name = model.getName();
        String key = paramType.getKey();

        // Expense Loop
        if (paramType == BotParamType.BUDGET_NEXT_EXPENSE_NAME) {
            ExpenseCategory category = new ExpenseCategoryBotDAO().findNext(botType);
            if (category != null)
                model.values.put(key, category.getName());
        }

        switch (name) {
            case "budget_create_q_savings_default": {
                // Nodes before Budget exists

                Map<String, Answer> answers = botRunner.getAnswerLog();
                switch (paramType) {
                    case BUDGET_DEFAULT_SAVINGS:
                        // Retrieve entered income
                        model.values.put(key, CurrencyHelper.format(Budget.calcDefaultSavings(
                                monthlyIncome(answers))));
                        break;
                    case BUDGET_DEFAULT_SAVING_PERCENT:
                        model.values.put(key, Double.toString(Math.round(Budget.DEFAULT_SAVING_PERCENT)));
                        break;
                }
                break;
            }

            default:
                // Nodes after Budget exists
                switch (paramType) {
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
                            model.values.put(key, CurrencyHelper.format(budget.getExpense()));
                        break;
                    case BUDGET_DEFAULT_SAVINGS:
                        if (budget != null)
                            model.values.put(key, CurrencyHelper.format(budget.getDefaultSavings()));
                        break;
                    default:
                        super.resolveParam(model, paramType);
                }
                break;
        }
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {
        switch (key) {
            case SET_TIP_QUERY:
                tipQuery();
                break;
            case ADD_EXPENSE:
                addNextExpense();
                break;
            default:
                super.onCall(key, answerLog, model);
        }
    }

    private void tipQuery() {
        if (activity == null)
            return;

        activity.setTipQuery(activity.getString(R.string.budget_create_qry_tip_income));
    }

    @Override
    public void onAsyncCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model, OnAsyncListener listener) {
        switch (key) {
            case DO_CREATE:
                doCreate(answerLog, listener);
                break;
            default:
                super.onAsyncCall(key, answerLog, model, listener);
                break;
        }
    }

    /**
     * Sends the new Budget to the server.
     */
    private void doCreate(@NonNull Map<String, Answer> answerLog,
                          @NonNull final OnAsyncListener listener) {

        Budget budget = new Budget();

        // Income
        budget.setIncome(monthlyIncome(answerLog));

        // Savings
        if (answerLog.containsKey(SAVING_DEFAULT_ACCEPT))
            // User accepts the default suggested savings
            budget.setSavings(budget.getDefaultSavings());
        else if (answerLog.containsKey(SAVINGS))
            // User entered their own savings amount
            budget.setSavings(Double.parseDouble(answerLog.get(SAVINGS).getValue()));

        // Expenses
        for (ExpenseCategory category : new ExpenseCategoryBotDAO().findSelected(botType)) {
            String key = EXPENSE_ANSWER_PREFIX + Long.toString(category.getId());
            double expense = 0.0;
            if (answerLog.containsKey(key))
                expense = Double.parseDouble(answerLog.get(key).getValue());

            budget.addExpense(new Expense(category, expense));
        }

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
                BudgetCreateController.this.budget = response.getBudget();

                // TODO: Badges from budget create
            }
        });
    }

    private void addNextExpense() {

        ExpenseCategory category = new ExpenseCategoryBotDAO().findNext(botType);

        // Stop Expense loop
        if (category == null) {
            // Special Node to stop the Expense loop and continue with the conversation
            Node node = new Node();
            node.setName(EXPENSE_STOP);
            node.setType(BotMessageType.DUMMY);
            node.setAutoNext("budget_create_do_create");

            botRunner.addNode(node);
            return;
        }

        Node node = new Node();
        node.setName(EXPENSE_QUESTION_PREFIX + Long.toString(category.getId()));
        node.setType(BotMessageType.TEXT);
        node.setProcessedText(getContext().getString(R.string.budget_create_q_expense_value));

        Answer answer = new Answer();
        answer.setName(EXPENSE_ANSWER_PREFIX + Long.toString(category.getId()));
        answer.setType(BotMessageType.INLINECURRENCY);
        answer.setTypeOnFinish("textCurrency");
        answer.setInlineEditHint("$(type_currency_hint)");
        answer.setInputKey(BotParamType.BUDGET_EXPENSE);

        // Loop back to this call
        answer.setNextOnFinish("budget_create_add_expense");

        node.setAutoAnswer(answer.getName());
        node.addAnswer(answer);

        node.finish();
        botRunner.addNode(node);
    }

    ////////////
    // Income //
    ////////////

    /**
     * @param answerLog The answer log, which must contain an income input.
     * @return The calculated monthly income. Will be 0.0 if no income input was provided.
     */
    private double monthlyIncome(@NonNull Map<String, Answer> answerLog) {
        if (answerLog.containsKey(INCOME))
            return Double.parseDouble(answerLog.get(INCOME).getValue());
        else {
            CrashlyticsHelper.log(TAG, "monthlyIncome",
                    "Unexpected attempt te get monthly income with no income input in conversation");
            return 0.0;
        }
    }

    ////////////////
    // Validation //
    ////////////////

    @Override
    public boolean validate(String name, String input) {
        if (input.startsWith(EXPENSE_ANSWER_PREFIX))
            return validateExpense(input);

        switch (name) {
            case "income_amount":
                return validateIncome(input);
            case "savings_amount":
                return validateSavings(input);
            default:
                return super.validate(name, input);
        }
    }

    private boolean validateIncome(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_income__empty);
            return false;
        }
        try {
            double income = Double.parseDouble(input);
            if (income <= 0.0) {
                toast(R.string.budget_create_err_income__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast("Invalid number");
            return false;
        }
    }

    private boolean validateSavings(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_savings__empty);
            return false;
        }
        try {
            double value = Double.parseDouble(input);

            // Find income defined earlier
            Map<String, Answer> answerLog = botRunner.getAnswerLog();
            Double income = null;
            if (answerLog.containsKey(INCOME))
                income = Double.parseDouble(answerLog.get(INCOME).getValue());

            if (value <= 0.0) {
                toast(R.string.budget_create_err_savings__zero);
                return false;
            } else if (income != null && value > income) {
                toast(R.string.budget_create_err_savings__gt_income);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.budget_create_err__invalid_number);
            return false;
        }
    }

    private boolean validateExpense(String input) {
        if (TextUtils.isEmpty(input)) {
            toast(R.string.budget_create_err_expense__empty);
            return false;
        }
        try {
            double expense = Double.parseDouble(input);

            if (expense <= 0) {
                toast(R.string.budget_create_err_expense__zero);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            toast(R.string.budget_create_err__invalid_number);
            return false;
        }
    }
}
