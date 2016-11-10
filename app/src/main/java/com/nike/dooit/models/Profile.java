package com.nike.dooit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by herman on 2016/11/05.
 */

public class Profile
{

    public static final int MALE = 0;
    public static final int FEMALE = 1;

    private Integer age;
    private Integer gender;
    private String mobile;
    @SerializedName("profile_image_url") private String profileImageUrl;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getMobile ()
    {
        return mobile;
    }

    public void setMobile (String mobile)
    {
        this.mobile = mobile;
    }

    public String getProfileImageUrl()
{
    return profileImageUrl;
}

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }
}