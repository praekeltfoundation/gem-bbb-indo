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

public class ChangeNameActivity extends DooitActivity {

    @BindView(org.gem.indo.dooit.R.id.activity_change_name_text_edit)
    EditText name;

    @BindView(org.gem.indo.dooit.R.id.activity_change_name_example_text_edit)
    TextView nameHint;

    @BindView(org.gem.indo.dooit.R.id.activity_change_name_button)
    Button changeNameButton;

    @Inject
    UserManager userManager;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_omboarding_change_name);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(org.gem.indo.dooit.R.id.activity_change_name_button)
    public void changeName() {
        if(!isNameValid())
            return;
        final User user = persisted.getCurrentUser();
        final String name = this.name.getText().toString();
        userManager.updateUser(user.getId(),name,new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(changeNameButton, msg, Snackbar.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                user.setUsername(name);
                ChangeNameActivity.this.persisted.setCurrentUser(user);
                ChangeNameActivity.this.finish();

            }
        });

    }
    public boolean isNameValid() {
        String nameText = name.getText().toString();
        ViewValidation.Result res = ViewValidation.isNameValid(nameText);
        if (!res.valid) {
            nameHint.setText(res.message);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(org.gem.indo.dooit.R.string.reg_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return res.valid;
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
