package org.gem.indo.dooit.views.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.views.RootActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.onboarding.ChangeNameActivity;
import org.gem.indo.dooit.views.onboarding.ChangePasswordActivity;

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
        setContentView(org.gem.indo.dooit.R.layout.activity_settings);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(org.gem.indo.dooit.R.drawable.ic_d_back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    @OnClick(R.id.settings_account_change_name)
    public void changeName(View view){
        ChangeNameActivity.Builder.create(this).startActivity();
    }
    @OnClick(R.id.settings_account_change_password)
    public void changePassword(View view){
        ChangePasswordActivity.Builder.create(this).startActivity();
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
                .setTitle(getString(org.gem.indo.dooit.R.string.title_activity_terms_and_conditions))
                .setUrl(Constants.TERMS_URL)
                .startActivity();
    }

    @OnClick({R.id.settings_about_privacy})
    public void privacy(View view) {
        SettingsWebViewActivity.Builder.create(this)
                .setTitle(getString(org.gem.indo.dooit.R.string.title_activity_privacy_policy))
                .setUrl(Constants.PRIVACY_URL)
                .startActivity();
    }


    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static Builder create(Context context) {
            Builder builder = new Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, SettingsActivity.class);
        }

    }
}
