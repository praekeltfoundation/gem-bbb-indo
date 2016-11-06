package com.nike.dooit.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;

import com.crashlytics.android.Crashlytics;
import com.nike.dooit.Constants;
import com.nike.dooit.helpers.permissions.PermissionsHelper;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by wsche on 2016/11/05.
 */

public class DooitActivity extends AppCompatActivity {

    @Inject
    PermissionsHelper permissionsHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Move this to where you establish a user session
        if (!Constants.DEBUG)
            logUser();
    }

    public void dismissDialog() {

    }

    public Locale getLocal() {
        return Locale.getDefault();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
