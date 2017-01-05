package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.validatior.UserValidator;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by chris on 2016-11-21.
 */

public class ChangeNameActivity extends DooitActivity {

    @BindString(R.string.profile_change_username_success)
    String successText;

    @BindView(R.id.activity_change_name_text_edit)
    EditText name;

    @BindView(R.id.activity_change_name_example_text_edit)
    TextView nameHint;

    @BindView(R.id.activity_change_name_button)
    Button changeNameButton;

    @Inject
    UserManager userManager;

    @Inject
    Persisted persisted;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_onboarding_change_name);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @OnClick(R.id.activity_change_name_button)
    public void changeName() {
        hideKeyboard();
        UserValidator uValidator = new UserValidator();
        if(!uValidator.isNameValid(this.name.getText().toString())) {
            nameHint.setText(uValidator.getResponseText());
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
            return;
        }
        else{
            nameHint.setText(R.string.reg_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme()));
        }

        final User user = persisted.getCurrentUser();
        final String name = this.name.getText().toString();
        userManager.updateUser(user.getId(),name,new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(changeNameButton, msg, Snackbar.LENGTH_LONG).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                Snackbar.make(changeNameButton, String.format(successText, name), Snackbar.LENGTH_LONG).show();
                user.setUsername(name);
                ChangeNameActivity.this.persisted.setCurrentUser(user);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChangeNameActivity.this.finish();
                    }
                }, 2000);
            }
        });

    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangeNameActivity.Builder create(Context context) {
            ChangeNameActivity.Builder builder = new ChangeNameActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangeNameActivity.class);
        }
    }
}
