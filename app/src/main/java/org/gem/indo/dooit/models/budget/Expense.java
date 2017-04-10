package org.gem.indo.dooit.models.budget;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.strings.StringHelper;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Wimpie Victor on 2017/03/03.
 */

public class Expense extends RealmObject {

    @PrimaryKey
    private long id;

    /**
     * The name of the Expense, as provided by the server.
     */
    @Nullable
    private String name;

    /**
     * The URL to the image, as provided by the server.
     */
    @SerializedName("image_url")
    @Nullable
    private String imageUrl;

    private double value;

    /**
     * The ID of the {@link ExpenseCategory}. Null means the Expense is custom.
     */
    @Nullable
    @SerializedName("category_id")
    private Long expenseCategoryId;

    public Expense() {
        // Required empty constructor
    }

    public Expense(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public Expense(ExpenseCategory category, double value) {
        this.expenseCategoryId = category.getId();
        this.value = value;
    }

    ////////
    // ID //
    ////////

    public long getId() {
        return id;
    }


    /////////////////
    // Category ID //
    /////////////////

    public long getCategoryId() {
        return (expenseCategoryId != null) ? expenseCategoryId : -1;
    }

    public boolean hasCategoryId() {
        return expenseCategoryId != null;
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

    public boolean hasName() {
        return !StringHelper.isEmpty(name);
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
