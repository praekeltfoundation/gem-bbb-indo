package com.rr.rgem.gem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.rr.rgem.gem.service.AuthStore;
import com.rr.rgem.gem.service.UrlStore;
import com.rr.rgem.gem.service.model.AuthToken;
import com.rr.rgem.gem.service.model.User;

/**
 * Created by chris on 9/8/2016.
 */
public class Persisted implements AuthStore, UrlStore {

    public final static String APP_PREFS = "dooit_prefs_";
    public final static String PREF_USER = "user";
    public final static String PREF_TOKEN = "token";
    public final static String PREF_URL = "url";
    public final static String TAG = "Persisted";

    final SharedPreferences settings;

    public Persisted(Context context){
        this.settings = context.getSharedPreferences(Persisted.APP_PREFS,0);
    }

    public Persisted(SharedPreferences settings){
        this.settings = settings;
    }

    public String loadConvState(String name){
        return settings.getString(name,"");
    }

    public void saveConvState(String name,String state){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name,state);
        editor.apply();
    }

    public User loadUser() {
        Gson g = new Gson();
        User user = g.fromJson(settings.getString(PREF_USER, "{}"), User.class);
        Log.d(TAG, String.format("Loading: %s", user));
        return user;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = settings.edit();
        Gson g = new Gson();
        String json = g.toJson(user);
        Log.d(TAG, String.format("Saving: %s", json));
        editor.putString(PREF_USER, json);
        editor.apply();
    }

    public void clearUser() {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREF_USER);
        editor.apply();
    }

    @Override
    public boolean hasToken() {
        return loadToken().hasToken();
    }

    public AuthToken loadToken() {
        Gson g = new Gson();
        AuthToken token = g.fromJson(settings.getString(PREF_TOKEN, "{}"), AuthToken.class);
        Log.d(TAG, String.format("Loadings: %s", token));
        return token;
    }

    public void saveToken(AuthToken token) {
        SharedPreferences.Editor editor = settings.edit();
        Gson g = new Gson();
        String json = g.toJson(token);
        Log.d(TAG, String.format("Saving: %s", json));
        editor.putString(PREF_TOKEN, json);
        editor.apply();
    }

    public void clearToken() {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREF_TOKEN);
        editor.apply();
    }

    public void setRegistered(boolean registered){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("registered", registered);
        if(registered){

        }
        // Commit the edits!
        editor.apply();
    }

    public boolean isRegistered(){
        boolean loggedIn = settings.getBoolean("registered", false);
        if(!loggedIn){
            //Toast.makeText(this,"The user has not registered",300);
        }
        return  loggedIn;
    }

    public void setLoggedIn(boolean loggedIn){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("loggedIn", loggedIn);

        // Commit the edits!
        editor.apply();
    }

    public boolean isLoggedIn(){
        boolean loggedIn = settings.getBoolean("loggedIn", false);
        if(!loggedIn){
            //Toast.makeText(this,"The user has not registered",300);
        }
        return  loggedIn;
    }

    @Override
    public String loadUrl() {
        return loadUrl("");
    }

    @Override
    public String loadUrl(String defaultUrl) {
        return settings.getString(PREF_URL, defaultUrl);
    }

    @Override
    public void saveUrl(String url) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_URL, url);
        editor.apply();
    }
}

