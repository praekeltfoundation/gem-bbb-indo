package com.nike.dooit.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nike.dooit.R;
import com.nike.dooit.views.profile.ProfileActivity;
import com.nike.dooit.views.welcome.WelcomeActivity;

public class RootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        startActivity(new Intent(this, ProfileActivity.class));
    }
}
