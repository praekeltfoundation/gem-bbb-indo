package com.nike.dooit.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nike.dooit.Constants;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.util.DooitSharedPreferences;
import com.nike.dooit.views.RootActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    DooitSharedPreferences dooitSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @OnClick({R.id.settings_account_sign_out})
    public void signOut(View view) {
        dooitSharedPreferences.clear();
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick({R.id.settings_about_terms})
    public void terms(View view) {
        SettingsWebViewActivity.Builder.create(this)
                .setTitle(getString(R.string.title_activity_terms_and_conditions))
                .setUrl(Constants.TERMS_URL)
                .startActivity();
    }

    @OnClick({R.id.settings_about_privacy})
    public void privacy(View view) {
        SettingsWebViewActivity.Builder.create(this)
                .setTitle(getString(R.string.title_activity_privacy_policy))
                .setUrl(Constants.PRIVACY_URL)
                .startActivity();
    }
}
