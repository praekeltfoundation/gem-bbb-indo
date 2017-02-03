package org.gem.indo.dooit.models.goal;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.String.StringHelper;

/**
 * Created by Wimpie Victor on 2016/11/28.
 */

public class GoalPrototype {

    private long id;
    private String name;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("num_users")
    private int numUsers;

    public GoalPrototype copy(){
        return new GoalPrototype(this.id, StringHelper.newString(this.name), StringHelper.newString(this.imageUrl), this.numUsers);
    }

    public int getNumUsers() {
        return numUsers;
    }

    public GoalPrototype() {
        // Blank Constructor
    }

    public GoalPrototype(long id, String name, String imageUrl, int numUsers) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.numUsers = numUsers;
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean hasImageUrl() {
        return !TextUtils.isEmpty(imageUrl);
    }

    public String getName() {
        return name;
    }
}
