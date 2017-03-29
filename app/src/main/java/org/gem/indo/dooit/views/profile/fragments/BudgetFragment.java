package org.gem.indo.dooit.views.profile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.profile.adapters.ExpenseAdapter;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public class BudgetFragment extends Fragment {

    @BindView(R.id.fragment_profile_budget_edit_container)
    View editContainerView;

    @BindView(R.id.fragment_profile_budget_create_container)
    View createContainerView;

    @BindView(R.id.fragment_profile_budget_create_btn)
    Button createBudgetBtn;

    @BindView(R.id.fragment_profile_budget_progress_container)
    View progressView;

    @BindView(R.id.fragment_profile_budget_recycler_view_expenses)
    RecyclerView expenseView;

    @BindView(R.id.fragment_profile_budget_error)
    TextView errorView;

    // Income

    @BindView(R.id.fragment_profile_budget_progress_income)
    ProgressBar incomeProgressView;

    @BindView(R.id.fragment_profile_budget_value_income)
    TextView incomeValueView;

    // Savings

    @BindView(R.id.fragment_profile_budget_progress_savings)
    ProgressBar savingsProgressView;

    @BindView(R.id.fragment_profile_budget_value_savings)
    TextView savingsValueView;

    @BindView(R.id.fragment_profile_budget_percent_savings)
    TextView savingsPercentView;

    // Left Over

    @BindView(R.id.fragment_profile_budget_progress_left_over)
    ProgressBar leftOverProgressView;

    @BindView(R.id.fragment_profile_budget_value_left_over)
    TextView leftOverValueView;

    @BindView(R.id.fragment_profile_budget_percent_left_over)
    TextView leftOverPercentView;

    @BindString(R.string.profile_error_retrieve_budget)
    String budgetRetrieveErrorMsg;

    @Inject
    BudgetManager budgetManager;

    private ExpenseAdapter adapter;

    public BudgetFragment() {

    }

    public static Fragment newInstance() {
        BudgetFragment fragment = new BudgetFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_budget, container, false);
        ButterKnife.bind(this, view);

        // Recycler View
        adapter = new ExpenseAdapter();
        expenseView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        expenseView.setAdapter(adapter);

        showProgressBar();
        budgetManager.retrieveBudgets(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Activity activity = getActivity();
                if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorMessage();
                        }
                    });
            }
        }).subscribe(new Action1<List<Budget>>() {
            @Override
            public void call(final List<Budget> budgets) {
                if (budgets == null)
                    return;
                Activity activity = getActivity();
                if (activity != null)
                    if (!budgets.isEmpty())
                        // User has created a budget
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Users are expected to only have one budget
                                populateBudget(budgets.get(0));
                                showBudgetEdit();
                            }
                        });
                    else {
                        // User has not created a budget
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Users are expected to only have one budget
                                showBudgetCreate();
                            }
                        });
                    }
            }
        });

        return view;
    }

    ////////////
    // Budget //
    ////////////

    private void populateBudget(Budget budget) {
        double income = budget.getIncome();
        double savings = budget.getSavings();
        double leftOver = budget.getLeftOver();

        // Income
        // Income progress bar is always full
        incomeProgressView.setProgress(100);
        incomeValueView.setText(CurrencyHelper.format(income));

        // Savings
        // Div by zero
        int savingsPercent = income != 0.0
                ? Math.max((int) ((savings / income) * 100.0), 0)
                : 0;
        savingsProgressView.setProgress(savingsPercent);
        savingsValueView.setText(CurrencyHelper.format(savings));
        savingsPercentView.setText(String.format(Locale.US, "%d%%", savingsPercent));

        // Expenses
        // Income is used to calculate expense percentages
        adapter.setIncome(budget.getIncome());
        adapter.replace(budget.getExpenses());

        // Left Over
        // Div by zero
        int leftOverPercentage = income != 0.0
                ? (int) ((leftOver / income) * 100.0) // Left over can be negative
                : 0;
        leftOverProgressView.setProgress(leftOverPercentage);
        leftOverValueView.setText(CurrencyHelper.format(leftOver));
        leftOverPercentView.setText(String.format(Locale.US, "%d%%", leftOverPercentage));
    }

    ////////
    // UI //
    ////////

    private void showProgressBar() {
        editContainerView.setVisibility(View.GONE);
        createContainerView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        progressView.setVisibility(View.GONE);
        editContainerView.setVisibility(View.GONE);
        createContainerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(budgetRetrieveErrorMsg);

        Context context = getContext();
        if (context != null)
            Toast.makeText(context, budgetRetrieveErrorMsg, Toast.LENGTH_SHORT).show();
    }

    private void showBudgetEdit() {
        progressView.setVisibility(View.GONE);
        editContainerView.setVisibility(View.VISIBLE);
        createContainerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void showBudgetCreate() {
        progressView.setVisibility(View.GONE);
        editContainerView.setVisibility(View.GONE);
        createContainerView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @OnClick(R.id.fragment_profile_budget_create_btn)
    void onCreateBudgetClick(Button button) {
        returnResult(RequestCodes.RESPONSE_BUDGET_CREATE);
    }

    @OnClick(R.id.fragment_profile_budget_edit_btn)
    void onEditBudgetClick(View view) {
        returnResult(RequestCodes.RESPONSE_BUDGET_EDIT);
    }

    private void returnResult(int resultCode) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        Activity parent = activity.getParent();
        if (parent != null)
            parent.setResult(resultCode);
        else
            activity.setResult(resultCode);
        activity.finish();
    }
}
