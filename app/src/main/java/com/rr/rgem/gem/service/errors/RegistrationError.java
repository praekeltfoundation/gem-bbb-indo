package com.rr.rgem.gem.service.errors;

import com.rr.rgem.gem.service.ErrorUtil;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/10/19.
 */
public class RegistrationError extends ErrorUtil.WebServiceError {

    List<String> username;

    List<String> profile;

    public List<String> getUsername() {
        return username;
    }

    public List<String> getProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "RegistrationError{" +
                "username=" + username +
                ", profile=" + profile +
                "} " + super.toString();
    }
}
