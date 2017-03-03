package org.gem.indo.dooit.models.budget;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2017/03/03.
 */

public class Expense {

    /**
     * The name of the Expense if its different from its {@link ExpenseCategory}. Null means it's
     */
    @Nullable
    private String name;

    private double value;

    /**
     * The ID of the {@link ExpenseCategory}. Null means the Expense is custom.
     */
    @Nullable
    @SerializedName("expense_category_id")
    private Long expenseCategoryId;
}
