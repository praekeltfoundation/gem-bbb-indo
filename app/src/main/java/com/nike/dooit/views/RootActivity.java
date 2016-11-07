package com.nike.dooit.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.util.Persisted;
import com.nike.dooit.views.main.MainActivity;
import com.nike.dooit.views.welcome.WelcomeActivity;

import javax.inject.Inject;

public class RootActivity extends AppCompatActivity {

    @Inject
    Persisted persisted;

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
