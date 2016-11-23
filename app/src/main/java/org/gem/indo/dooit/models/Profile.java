package org.gem.indo.dooit.models;

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

    public String getMobile ()
    {
        return mobile;
    }

    public void setMobile (String mobile)
    {
        this.mobile = mobile;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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