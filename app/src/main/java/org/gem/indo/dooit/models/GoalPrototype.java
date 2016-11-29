package org.gem.indo.dooit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2016/11/28.
 */

public class GoalPrototype {

    private long id;
    private String name;
    @SerializedName("image_url")
    private String imageUrl;

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }
}
