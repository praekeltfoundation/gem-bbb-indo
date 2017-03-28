package org.gem.indo.dooit.views.profile.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public class ExpenseViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_profile_budget_info_value_expense)
    TextView valueView;

    @BindView(R.id.item_view_profile_budget_info_progress_expense)
    ProgressBar progressView;

    @BindView(R.id.item_view_profile_budget_info_percent_expense)
    TextView percentView;

    public ExpenseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(double income, @NonNull Expense expense) {
        reset();

        valueView.setText(CurrencyHelper.format(expense.getValue()));

        int progress = (int) Math.max(income != 0.0 ? (expense.getValue() / income) * 100.0 : 0.0, 0.0);
        progressView.setProgress(progress);
        percentView.setText(String.format(Locale.US, "%d%%", progress));
    }

    private void reset() {
        valueView.setText(null);
        progressView.setProgress(0);
        percentView.setText(null);
    }
}
