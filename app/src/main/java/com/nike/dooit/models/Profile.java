package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by herman on 2016/11/05.
 */

public class Profile
{
    private String id;
    @SerializedName("profile_image_url") private String profileImageUrl;
    private String mobile;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getProfileImageUrl()
{
    return profileImageUrl;
}

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public String getMobile ()
    {
        return mobile;
    }

    public void setMobile (String mobile)
    {
        this.mobile = mobile;
    }
}