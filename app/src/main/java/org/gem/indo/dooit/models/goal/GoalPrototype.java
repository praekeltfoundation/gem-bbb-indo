package org.gem.indo.dooit.models.goal;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2016/11/28.
 */

public class GoalPrototype {

    private long id;
    private String name;
    @SerializedName("image_url")
    private String imageUrl;

    public GoalPrototype() {
        // Blank Constructor
    }

    public GoalPrototype(long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
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
