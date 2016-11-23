package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.ViewValidation;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by chris on 2016-11-21.
 */

public class ChangePasswordActivity extends DooitActivity {


    @BindView(org.gem.indo.dooit.R.id.activity_change_password_edit_text)
    EditText password;

    @BindView(org.gem.indo.dooit.R.id.activity_change_password_example_text_edit)
    TextView passwordHint;

    @BindView(org.gem.indo.dooit.R.id.activity_change_password_old_edit_text)
    EditText passwordOld;

    @Inject
    UserManager userManager;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_onboarding_change_password);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
    }

    public boolean isPasswordValid() {
        boolean valid = true;
        ViewValidation.Result result = ViewValidation.isPasswordValid(password.getText().toString());
        if (passwordOld.getText().toString().equals(password.getText().toString())) {
            valid = false;
            passwordHint.setText(org.gem.indo.dooit.R.string.reg_change_password_error_1);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if(result.valid){
            passwordHint.setText(org.gem.indo.dooit.R.string.reg_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }else{
            passwordHint.setText(result.message);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        }
        return valid;
    }
    @BindView(org.gem.indo.dooit.R.id.activity_change_password_button)
    Button changePasswordButton;

    @OnClick(org.gem.indo.dooit.R.id.activity_change_password_button)
    public void changePassword() {
        if (!isPasswordValid())
            return;
        final User user = persisted.getCurrentUser();
        final String newPassword = this.password.getText().toString();
        final String oldPassword = this.passwordOld.getText().toString();
        userManager.changePassword(user.getId(),oldPassword,newPassword,new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(changePasswordButton, msg, Snackbar.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                user.setPassword(newPassword);
                ChangePasswordActivity.this.persisted.setCurrentUser(user);
                ChangePasswordActivity.this.finish();

            }
        });
        ChangePasswordActivity.this.finish();
    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangePasswordActivity.Builder create(Context context) {
            ChangePasswordActivity.Builder builder = new ChangePasswordActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangePasswordActivity.class);
        }
    }
}