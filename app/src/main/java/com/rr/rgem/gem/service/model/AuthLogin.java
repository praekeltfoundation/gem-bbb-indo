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

    public static AuthLogin fromUser(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (username == null) {
            throw new NullPointerException("Username is null");
        }

        if (password == null) {
            throw new NullPointerException("Password is null");
        }

        return new AuthLogin(username, password);
    }

    @Override
    public String toString() {
        return "AuthLogin{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
