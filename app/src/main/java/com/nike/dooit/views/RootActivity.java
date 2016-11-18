package com.nike.dooit.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.views.main.MainActivity;
import com.nike.dooit.views.welcome.WelcomeActivity;

import javax.inject.Inject;

public class RootActivity extends AppCompatActivity {

    @Inject
    Persisted persisted;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);

        //If the user has logged in
        if (persisted.hasToken()) {
            MainActivity.Builder.create(this).startActivityClearTop();
        } else {
            WelcomeActivity.Builder.create(this).startActivityClearTop();
        }
    }
}
