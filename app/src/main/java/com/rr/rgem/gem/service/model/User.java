package com.rr.rgem.gem.service.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chris on 2016/10/12.
 */
public class User {

    private static final int NO_ID = -1;

    int id = NO_ID;

    String username;

    String password;

    @SerializedName("first_name")
    String firstName;

    @SerializedName("last_name")
    String lastName;

    String email;

    Profile profile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getLabel() {
        if (firstName != null) {
            if (lastName != null) {
                return firstName + " " + lastName;
            } else {
                return firstName;
            }
        } else {
            return username;
        }
    }

    @Override
    public String toString() {
        return String.format("User {%s <%s>}", username, email);
    }
}