package com.rr.rgem.gem.service.model;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class AuthToken {

    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return String.format("AuthToken{%s}", token);
    }
}
