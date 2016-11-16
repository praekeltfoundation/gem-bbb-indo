package com.nike.dooit.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Singleton;

/**
 * Created by herman on 2016/11/05.
 */
@Singleton
public class DooitSharedPreferences {

    private static final String SHARED_PREFERENCES_NAME = "dooit-shared-preferences";

    public Context context;

    public DooitSharedPreferences(Context context) {
        this.context = context;
    }

    private SharedPreferences sharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean containsKey(String key) {
        return sharedPreferences().contains(key);
    }

    public String getString(String key, String def) {
        return sharedPreferences().getString(key, def);
    }

    public Set<String> getStringSet(String key, Set<String> def) {
        return sharedPreferences().getStringSet(key, def);
    }

    public Integer getInteger(String key, Integer def) {
        return sharedPreferences().getInt(key, def);
    }

    public Long getLong(String key, Long def) {
        return sharedPreferences().getLong(key, def);
    }

    public Float getFloat(String key, Float def) {
        return sharedPreferences().getFloat(key, def);
    }

    public Double getDouble(String key, Double def) {
        return (double) sharedPreferences().getFloat(key, def.floatValue());
    }

    public Boolean getBoolean(String key, Boolean def) {
        return sharedPreferences().getBoolean(key, def);
    }

    public <T> T getComplex(String key, Class<T> clazz) {
        return new Gson().fromJson(getString(key, ""), clazz);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.remove(key);
        editor.apply();
    }

    public void setInteger(String key, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLong(String key, Long value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void setFloat(String key, Float value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void setDouble(String key, Double value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putFloat(key, value.floatValue());
        editor.apply();
    }

    public void setBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setComplex(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences().edit();
        editor.putString(key, new Gson().toJson(value));
        editor.apply();
    }

    public void clear() {
        for (String key : sharedPreferences().getAll().keySet()) {
            remove(key);
        }
    }
}
