package org.gem.indo.dooit.views.profile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.budget.Expense;
import org.gem.indo.dooit.views.profile.viewholders.ExpenseViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

    private List<Expense> expenses = new ArrayList<>();
    private double income = 0.0;

    public ExpenseAdapter() {

    }

    /////////////
    // Adapter //
    /////////////

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_profile_budget_expense,
                parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        holder.populate(income, getItem(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    ///////////////
    // Container //
    ///////////////

    public Expense getItem(int position) {
        return expenses.get(position);
    }

    public void replace(Collection<Expense> expenses) {
        this.expenses.clear();
        this.expenses.addAll(expenses);
        notifyDataSetChanged();
    }

    ////////////
    // Income //
    ////////////

    public void setIncome(double income) {
        this.income = income;
    }
}
