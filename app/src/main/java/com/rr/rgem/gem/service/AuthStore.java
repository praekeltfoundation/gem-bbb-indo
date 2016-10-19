package com.rr.rgem.gem.service;

import com.rr.rgem.gem.service.model.AuthToken;

/**
 * Created by Wimpie Victor on 2016/10/19.
 */
public interface AuthStore {
    boolean hasToken();
    AuthToken loadToken();
    void saveToken(AuthToken token);
}
