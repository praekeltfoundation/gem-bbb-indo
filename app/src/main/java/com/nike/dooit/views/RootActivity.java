package com.nike.dooit.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nike.dooit.R;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.util.DooitSharedPreferences;
import com.nike.dooit.views.main.MainActivity;
import com.nike.dooit.R;
import com.nike.dooit.views.profile.ProfileActivity;
import com.nike.dooit.views.welcome.WelcomeActivity;

import javax.inject.Inject;

public class RootActivity extends AppCompatActivity {

    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);

        //If the user has logged in
        if (dooitSharedPreferences.containsKey(DooitSharedPreferences.TOKEN)) {
            MainActivity.Builder.create(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).startActivity();
        } else {
            WelcomeActivity.Builder.create(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).startActivity();
        }
    }
}
