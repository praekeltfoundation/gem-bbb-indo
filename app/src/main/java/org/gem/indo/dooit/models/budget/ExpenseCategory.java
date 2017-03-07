package org.gem.indo.dooit.models.budget;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Holds the content of an expense.
 * <p>
 * Created by Wimpie Victor on 2017/03/03.
 */

public class ExpenseCategory extends RealmObject {

    @PrimaryKey
    private long id;

    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    ////////
    // ID //
    ////////

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
