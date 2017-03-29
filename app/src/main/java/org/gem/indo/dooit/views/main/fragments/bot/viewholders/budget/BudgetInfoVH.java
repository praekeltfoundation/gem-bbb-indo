package org.gem.indo.dooit.views.main.fragments.bot.viewholders.budget;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.budget.ExpenseInfoAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.BaseBotViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2017/03/22.
 */

public class BudgetInfoVH extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_budget_info)
    View backgroundView;

    @BindView(R.id.item_view_bot_budget_info_value_savings)
    TextView savingsValueView;

    @BindView(R.id.item_view_bot_budget_info_progress_savings)
    ProgressBar savingsProgressView;

    @BindView(R.id.item_view_bot_budget_info_value_left_over)
    TextView leftOverValueView;

    @BindView(R.id.item_view_bot_budget_info_progress_left_over)
    ProgressBar leftOverProgressView;

    @BindView(R.id.item_view_bot_budget_info_recycler_view_expenses)
    RecyclerView expenseRecycler;

    @BindString(R.string.budget_line_of_income)
    String ofIncome;

    private ExpenseInfoAdapter adapter;
    private BotAdapter botAdapter;

    public BudgetInfoVH(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;

        adapter = new ExpenseInfoAdapter();
        expenseRecycler.setAdapter(adapter);
        expenseRecycler.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                LinearLayoutManager.VERTICAL, false));

        backgroundView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        if (dataModel == null)
            return;

        try {
            double income = dataModel.values.getDouble(BotParamType.BUDGET_INCOME.getKey(), 0.0);
            double savings = dataModel.values.getDouble(BotParamType.BUDGET_SAVINGS.getKey(), 0.0);
            double leftOver = dataModel.values.getDouble(BotParamType.BUDGET_TOTAL_EXPENSES_REMAINDER.getKey(), 0.0);
            String[] expenseData = dataModel.values.getStringArray(BotParamType.BUDGET_EXPENSES.getKey());

            // Income
            // Income is no longer shown in
//            incomeProgressView.setProgress(100);
//            incomeValueView.setText(CurrencyHelper.format(income));

            // Savings
            // Div by zero
            int savingsPercent = income != 0.0
                    ? Math.max((int) ((savings / income) * 100.0), 0)
                    : 0;
            savingsProgressView.setProgress(savingsPercent);
            savingsValueView.setText(CurrencyHelper.format(savings));
//            savingsPercentView.setText(String.format(Locale.US, "%d%%", savingsPercent));

            // Expenses
            if (expenseData != null) {
                int expenseCount = expenseData.length / 3;
                List<BudgetInfoExpenseVH.ExpenseViewModel> expenses = new ArrayList<>();
                try {
                    for (int i = 0; i < expenseCount; i++) {
                        expenses.add(new BudgetInfoExpenseVH.ExpenseViewModel(
                                expenseData[3 * i],
                                Double.parseDouble(expenseData[3 * i + 1]),
                                Integer.parseInt(expenseData[3 * i + 2])
                        ));
                    }
                    adapter.replace(expenses);
                } catch (NumberFormatException e) {
                    CrashlyticsHelper.logException(e);
                }
            }

            // LeftOver
            // Div by zero
            int leftOverPercentage = income != 0.0
                    ? (int) ((leftOver / income) * 100.0) // Left over can be negative
                    : 0;
            // Prevent progress bar from wrapping
            leftOverProgressView.setProgress(Math.max(leftOverPercentage, 0));
            leftOverValueView.setText(CurrencyHelper.format(leftOver));
//            leftOverPercentView.setText(String.format(Locale.US, "%d%%", leftOverPercentage));
        } catch (NullPointerException | ClassCastException e) {
            CrashlyticsHelper.logException(e);
        }
    }

    @Override
    public void reset() {
//        incomeProgressView.setProgress(0);
        savingsProgressView.setProgress(0);
        leftOverProgressView.setProgress(0);
    }

    @Override
    protected void populateModel() {
        BotController controller = botAdapter.getController();

        if (controller == null)
            return;

        Budget budget = (Budget) controller.getObject(BotObjectType.BUDGET);
        if (budget == null || dataModel == null)
            return;

        dataModel.values.put(BotParamType.BUDGET_INCOME.getKey(), budget.getIncome());
        dataModel.values.put(BotParamType.BUDGET_SAVINGS.getKey(), budget.getSavings());
        dataModel.values.put(BotParamType.BUDGET_TOTAL_EXPENSES_REMAINDER.getKey(), budget.getLeftOver());

        // Expense details are packed into a String array because of limitations of the value map
        double income = budget.getIncome();
        String[] expenses = new String[budget.getExpenses().size() * 3];
        for (int i = 0; i < budget.getExpenses().size(); i++) {
            Expense expense = budget.getExpenses().get(i);
            expenses[3 * i] = expense.getName();
            expenses[3 * i + 1] = Double.toString(expense.getValue());
            expenses[3 * i + 2] = Integer.toString(income != 0.0
                    ? Math.max((int) ((expense.getValue() / income) * 100.0), 0)
                    : 0);
        }
        dataModel.values.put(BotParamType.BUDGET_EXPENSES.getKey(), expenses);
    }
}
