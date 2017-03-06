package org.gem.indo.dooit.models.budget;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Wimpie Victor on 2017/03/03.
 */

public class Expense extends RealmObject {

    @PrimaryKey
    private long id;

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

    ////////
    // ID //
    ////////

    public long getId() {
        return id;
    }

    //////////
    // Name //
    //////////


    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    ///////////
    // Value //
    ///////////

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
