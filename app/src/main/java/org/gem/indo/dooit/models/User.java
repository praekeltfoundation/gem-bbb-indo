package org.gem.indo.dooit.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by herman on 2016/11/05.
 */
public class User {

    private static final long NO_ID = -1;

    private long id = NO_ID;
    private String username;
    private String password;
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private Profile profile;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean hasId() {
        return id != NO_ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasEmail() {
        return !TextUtils.isEmpty(email);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean hasFirstName() {
        return !TextUtils.isEmpty(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
        if (hasFirstName())
            return firstName;
        return username;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean hasProfile() {
        return profile != null;
    }

    public boolean hasProfileImage() {
        return profile != null && !TextUtils.isEmpty(profile.getProfileImageUrl());
    }
}


