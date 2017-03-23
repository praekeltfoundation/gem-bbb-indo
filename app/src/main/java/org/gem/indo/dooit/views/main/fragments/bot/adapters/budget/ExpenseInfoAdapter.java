package org.gem.indo.dooit.views.main.fragments.bot.adapters.budget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.budget.BudgetInfoExpenseVH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/03/22.
 */

public class ExpenseInfoAdapter extends RecyclerView.Adapter<BudgetInfoExpenseVH> {

    private List<BudgetInfoExpenseVH.ExpenseViewModel> expenses = new ArrayList<>();

    public ExpenseInfoAdapter() {

    }

    /////////////
    // Adapter //
    /////////////

    @Override
    public BudgetInfoExpenseVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_bot_budget_info_expense, parent, false);
        return new BudgetInfoExpenseVH(view);
    }

    @Override
    public void onBindViewHolder(BudgetInfoExpenseVH holder, int position) {
        holder.populate(getItem(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    ///////////////
    // Container //
    ///////////////

    private BudgetInfoExpenseVH.ExpenseViewModel getItem(int index) {
        return expenses.get(index);
    }

    public void replace(Collection<BudgetInfoExpenseVH.ExpenseViewModel> expenses) {
        this.expenses.clear();
        this.expenses.addAll(expenses);
        notifyDataSetChanged();
    }
}
