package org.gem.indo.dooit.models.budget;

import com.google.gson.annotations.SerializedName;

/**
 * Holds the content of an expense.
 *
 * Created by Wimpie Victor on 2017/03/03.
 */

public class ExpenseCategory {

    private String name;

    @SerializedName("image_url")
    private String imageUrl;
}
