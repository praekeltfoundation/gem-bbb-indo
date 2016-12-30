package org.gem.indo.dooit.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class Badge {

    private String name;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("earned_on")
    private DateTime earnedOn;
    @SerializedName("social_url")
    private String socialUrl;
    private String intro;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Helper to create name consistent with other dialogue graph names.
     *
     * @return
     */
    public String getGraphName() {
        return getName().toLowerCase().replace(' ', '_');
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public DateTime getEarnedOn() {
        return earnedOn;
    }

    public String getSocialUrl() {
        return socialUrl;
    }

    public boolean hasSocialUrl() {
        return !TextUtils.isEmpty(socialUrl);
    }

    public String getIntro() {
        return intro;
    }

    public boolean hasIntro() {
        return !TextUtils.isEmpty(intro);
    }
}
