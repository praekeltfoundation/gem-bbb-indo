package com.nike.dooit.models;

/**
 * Created by herman on 2016/11/05.
 */

public class Profile
{
    private String id;
    private String profile_image_url;
    private String mobile;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getProfile_image_url ()
{
    return profile_image_url;
}

    public void setProfile_image_url (String profile_image_url)
    {
        this.profile_image_url = profile_image_url;
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