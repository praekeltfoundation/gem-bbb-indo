package com.rr.rgem.gem.service.model;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class AuthToken {

    private static String TOKEN_HEADER_TEMPLATE = "Token %s";

    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns the token formatted for an HTTP header.
     */
    public String getTokenHeader() {
        return String.format(TOKEN_HEADER_TEMPLATE, token);
    }

    @Override
    public String toString() {
        return String.format("AuthToken{%s}", token);
    }
}
