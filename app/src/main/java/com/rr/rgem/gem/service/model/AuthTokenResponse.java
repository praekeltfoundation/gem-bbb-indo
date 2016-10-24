package com.rr.rgem.gem.service.model;

/**
 * Created by Wimpie Victor on 2016/10/24.
 */
public class AuthTokenResponse {

    AuthToken token;

    User user;

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthTokenResponse{" +
                "token=" + token.getToken() +
                ", user=" + user.getUsername() +
                '}';
    }
}
