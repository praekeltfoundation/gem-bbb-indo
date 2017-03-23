package org.gem.indo.dooit.views.main.fragments.bot.viewholders.budget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2017/03/22.
 */

public class BudgetInfoExpenseVH extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_budget_info_progress_expense)
    ProgressBar expenseProgress;

    @BindView(R.id.item_view_bot_budget_info_value_expense)
    TextView expenseValue;

    @BindView(R.id.item_view_bot_budget_info_percent_expense)
    TextView expensePercentage;

    public BudgetInfoExpenseVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(ExpenseViewModel expense) {
        expenseValue.setText(String.format("%s - %s",
                expense.getName(), CurrencyHelper.format(expense.getValue())));

        expenseProgress.setProgress(expense.getPercentage());

        expensePercentage.setText(String.format(Locale.US, "%d%%",
                expense.getPercentage()));
    }

    public static class ExpenseViewModel {

        private String name;
        private double value;
        private int percentage;

        ExpenseViewModel(String name, double value, int percentage) {
            this.name = name;
            this.value = value;
            this.percentage = percentage;
        }

        String getName() {
            return name;
        }

        double getValue() {
            return value;
        }

        int getPercentage() {
            return percentage;
        }
    }
}
