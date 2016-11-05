package com.nike.dooit.models;

import java.io.Serializable;

/**
 * Created by herman on 2016/11/05.
 */
public class User implements Serializable {
    private String is_staff;
    private String is_active;
    private String is_superuser;
    private String last_login;
    private String id;
    private String[] user_permissions;
    private String first_name;
    private String username;
    private String date_joined;
    private String email;
    private String last_name;
    private String[] groups;
    private Profile profile;

    public String getIs_staff() {
        return is_staff;
    }

    public void setIs_staff(String is_staff) {
        this.is_staff = is_staff;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getIs_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(String is_superuser) {
        this.is_superuser = is_superuser;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getUser_permissions() {
        return user_permissions;
    }

    public void setUser_permissions(String[] user_permissions) {
        this.user_permissions = user_permissions;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(String date_joined) {
        this.date_joined = date_joined;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}


