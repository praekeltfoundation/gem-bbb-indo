package com.rr.rgem.gem.service.model;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */

public class AuthLogin {

    String username;
    String password;

    public AuthLogin(String username, String password) {
        this.username = username;
        this.password = password;
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
}
