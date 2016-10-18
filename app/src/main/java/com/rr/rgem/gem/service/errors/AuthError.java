package com.rr.rgem.gem.service.errors;

import com.rr.rgem.gem.service.ErrorUtil;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/10/18.
 */
public class AuthError extends ErrorUtil.WebServiceError {

    List<String> username;
    List<String> password;

    public List<String> getUsername() {
        return username;
    }

    public List<String> getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "AuthError{" +
                "status=" + status +
                ", username=" + username +
                ", password=" + password +
                ", detail=" + detail +
                ", non_field_errors=" + nonFieldErrors +
                '}';
    }
}
