package com.nike.dooit.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.util.DooitSharedPreferences;
import com.nike.dooit.views.RootActivity;

import javax.inject.Inject;

import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((DooitApplication) getApplication()).component.inject(this);
    }

    @OnClick(R.id.settings_account_sign_out)
    public void signOut(View view) {
        dooitSharedPreferences.clear();
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.settings_about_terms)
    public void terms(View view) {

    }
}
