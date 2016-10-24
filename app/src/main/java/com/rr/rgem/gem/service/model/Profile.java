package com.rr.rgem.gem.service.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class Profile {

    String mobile;

    @SerializedName("profile_image_url")
    String profileImageUrl;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean hasProfileImage() {
        return profileImageUrl != null;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
