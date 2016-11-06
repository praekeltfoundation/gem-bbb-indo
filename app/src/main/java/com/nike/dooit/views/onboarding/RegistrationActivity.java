package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.models.Profile;
import com.nike.dooit.models.User;
import com.nike.dooit.util.Persisted;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends DooitActivity {

    @BindView(R.id.activity_registration_t_c_text_view)
    TextView textViewTC;

    @BindView(R.id.activity_registration_login_text_view)
    TextView textViewLogin;

    @BindView(R.id.activity_registration_name_text_edit)
    EditText name;

    @BindView(R.id.activity_registration_password_edit_text)
    EditText password;

    @BindView(R.id.activity_registration_number_text_edit)
    EditText number;

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        Spannable spanTc = new SpannableString(getString(R.string.reg_t_c));
        Spannable spanLogin = new SpannableString(getString(R.string.already_registered_log_in));

        if (!getLocal().getCountry().equals("in")) {
            spanTc.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.pink, getTheme())), spanTc.length() - 17, spanTc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanLogin.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.pink, getTheme())), spanLogin.length() - 6, spanLogin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewTC.setText(spanTc);
        textViewLogin.setText(spanLogin);
    }

    @OnClick(R.id.activity_registration_t_c_text_view)
    public void openTC() {

    }

    @OnClick(R.id.activity_registration_register_button)
    public void navigateToProfileImage() {
        ProfileImageActivity.Builder.create(this, name.getText().toString(), password.getText().toString(), number.getText().toString()).startActivity();
    }


    @OnClick(R.id.activity_registration_login_text_view)
    public void openLogin() {
        LoginActivity.Builder.create(this).startActivity();
    }

    public User getUser() {
        User user = new User();
        user.setUsername(name.getText().toString());
        user.setPassword(password.getText().toString());
        Profile profile = new Profile();
        profile.setMobile(number.getText().toString());
        user.setProfile(profile);
        return user;
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
            return new Intent(context, RegistrationActivity.class);
        }

    }
}
