package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2017/03/22.
 */

public class BudgetInfoViewHolder extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_budget_info)
    View background;

    @BindView(R.id.item_view_bot_budget_info_progress_income)
    ProgressBar incomeProgress;

    @BindView(R.id.item_view_bot_budget_info_progress_savings)
    ProgressBar savingsProgress;

    @BindView(R.id.item_view_bot_budget_info_progress_left_over)
    ProgressBar leftOverProgress;

    @BindString(R.string.budget_line_of_income)
    String ofIncome;

    private BotAdapter botAdapter;

    public BudgetInfoViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;

        background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));
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

            // Income bar is always full
            incomeProgress.setProgress(100);

            // Div by zero
            if (income != 0.0)
                savingsProgress.setProgress(Math.max((int) ((savings / income) * 100.0), 0));

            // Div by zero
            if (income != 0.0)
                leftOverProgress.setProgress(Math.max((int) ((leftOver / income) * 100.0), 0));
        } catch (NullPointerException | ClassCastException e) {
            CrashlyticsHelper.logException(e);
        }
    }

    @Override
    public void reset() {
        incomeProgress.setProgress(0);
        savingsProgress.setProgress(0);
        leftOverProgress.setProgress(0);
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
        dataModel.values.put(BotParamType.BUDGET_REMAINING_EXPENSES.getKey(), budget.getLeftOver());
    }
}
