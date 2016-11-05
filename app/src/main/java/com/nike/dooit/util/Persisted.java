package com.nike.dooit.util;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.models.Token;
import com.nike.dooit.models.User;

import javax.inject.Inject;

/**
 * Created by chris on 9/8/2016.
 */
public class Persisted {
    private static final String TOKEN = "token";
    private static final String USER = "user";
    private static final String TAG = "Persisted";
    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Inject
    public Persisted(Application application) {
        ((DooitApplication) application).component.inject(this);
    }

    public String loadConvState(String name) {
        return dooitSharedPreferences.getString(name, "");
    }

    public void saveConvState(String name, String state) {
        dooitSharedPreferences.setString(name, state);
    }

    public User loadUser() {
        User user = dooitSharedPreferences.getComplex(USER, User.class);
        Log.d(TAG, String.format("Loading: %s", user));
        return user;
    }

    public void saveUser(User user) {
        dooitSharedPreferences.setComplex(USER, user);
        Log.d(TAG, String.format("Saving: %s", user));
    }

    public void clearUser() {
        dooitSharedPreferences.remove(USER);
    }

    //    @Override
    public boolean hasToken() {
        Token token = loadToken();
        return token != null && !TextUtils.isEmpty(token.getToken());
    }

    public Token loadToken() {
        Token token = dooitSharedPreferences.getComplex(TOKEN, Token.class);
        Log.d(TAG, String.format("Loadings: %s", token));
        return token;
    }

    public String getToken() {
        return loadToken().getToken();
    }

    public void saveToken(Token token) {
        dooitSharedPreferences.setComplex(TOKEN, token);
        Log.d(TAG, String.format("Saving: %s", token));
    }

    public void clearToken() {
        dooitSharedPreferences.remove(TOKEN);
    }
}

