package com.nike.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.responses.AuthenticationResponse;
import com.nike.dooit.api.responses.OnboardingResponse;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.Profile;
import com.nike.dooit.models.User;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;

import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class RegistrationActivity extends DooitActivity {

    private static final int MIN_AGE = 12;
    private static final int MAX_AGE = 80;
    private static final String NAME_PATTERN = "[a-zA-Z0-9@\\.\\=\\-\\_]+";
    private static final String MOBILE_PATTERN = "^\\+?1?\\d{9,15}$";
    private static final int MAX_NAME_LENGTH = 150;
    private static final int MIN_MOBILE_LENGTH = 9;
    private static final int MAX_MOBILE_LENGTH = 16;

    @BindView(R.id.activity_registration_t_c_text_view)
    TextView textViewTC;

    @BindView(R.id.activity_registration_login_text_view)
    TextView textViewLogin;

    @BindView(R.id.activity_registration_name_text_edit)
    EditText name;

    @BindView(R.id.activity_registration_age_spinner)
    Spinner age;

    @BindView(R.id.activity_registration_gender_radiogroup)
    RadioGroup gender;

    @BindView(R.id.activity_registration_password_edit_text)
    EditText password;

    @BindView(R.id.activity_registration_number_text_edit)
    EditText number;

    @BindView(R.id.activity_registration_name_example_text_edit)
    TextView nameHint;

    @BindView(R.id.activity_registration_age_example_text_view)
    TextView ageHint;

    @BindView(R.id.activity_registration_number_example_text_edit)
    TextView numberHint;

    @BindView(R.id.activity_registration_password_example_text_edit)
    TextView passwordHint;

    @BindView(R.id.activity_registration_register_button)
    Button registerButton;


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

        // Age
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        for (int i = MIN_AGE; i <= MAX_AGE; i++)
            ageAdapter.add(i);
        age.setAdapter(ageAdapter);
        age.setSelection(4); // 16

        // Default gender
        gender.check(R.id.activity_registration_gender_girl);

        if (!getLocal().getCountry().equals("in")) {
            spanTc.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.pink, getTheme())), spanTc.length() - 17, spanTc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanLogin.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.pink, getTheme())), spanLogin.length() - 6, spanLogin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewTC.setText(spanTc);
        textViewLogin.setText(spanLogin);
    }

    @OnClick(R.id.activity_registration_t_c_text_view)
    public void openTC() {
        TermsConditionsActivity.Builder.create(this)
                .startActivity();
    }

    @OnClick(R.id.activity_registration_register_button)
    public void register() {

        if (!detailsValid())
            return;

        hideKeyboard();

        authenticationManager.onboard(getUser(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(registerButton, msg, Snackbar.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<OnboardingResponse>() {
            @Override
            public void call(OnboardingResponse onboardingResponse) {
                authenticationManager.login(getUser().getUsername(), getUser().getPassword(), new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {
                        for (String msg : error.getErrorMessages())
                            Snackbar.make(registerButton, msg, Snackbar.LENGTH_SHORT).show();
                    }
                }).subscribe(new Action1<AuthenticationResponse>() {
                    @Override
                    public void call(AuthenticationResponse authenticationResponse) {
                        persisted.setCurrentUser(authenticationResponse.getUser());
                        persisted.saveToken(authenticationResponse.getToken());
                        ProfileImageActivity.Builder.create(RegistrationActivity.this).startActivityClearTop();
                    }
                });

            }
        });
    }

    private boolean detailsValid() {
        return isNameValid() & isAgeValid() & isNumberValid() & isPasswordValid();
    }


    @OnClick(R.id.activity_registration_login_text_view)
    public void openLogin() {
        LoginActivity.Builder.create(this).startActivityClearTop();
    }

    public User getUser() {
        User user = new User();
        user.setUsername(name.getText().toString());
        user.setPassword(password.getText().toString());

        Profile profile = new Profile();
        profile.setMobile(number.getText().toString());
        profile.setAge((Integer) age.getSelectedItem());

        switch (gender.getCheckedRadioButtonId()) {
            case R.id.activity_registration_gender_boy:
                profile.setGender(Profile.MALE);
                break;
            case R.id.activity_registration_gender_girl:
                profile.setGender(Profile.FEMALE);
                break;
        }

        user.setProfile(profile);

        return user;
    }

    public boolean isNameValid() {
        boolean valid = true;
        String nameText = name.getText().toString();
        if (TextUtils.isEmpty(nameText)) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_1);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (nameText.contains(" ")) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_2);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (!Pattern.compile(NAME_PATTERN).matcher(nameText).matches()) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_3);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (nameText.length() > MAX_NAME_LENGTH) {
            valid = false;
            nameHint.setText(R.string.reg_example_name_error_4);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(R.string.reg_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isAgeValid() {
        boolean valid;
        valid = true;
        if (!valid) {
            ageHint.setText(R.string.reg_example_age_error_1);
            ageHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            ageHint.setText(R.string.reg_example_age);
            ageHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isNumberValid() {
        boolean valid = true;
        String numberText = number.getText().toString();
        if (TextUtils.isEmpty(numberText)) {
            valid = false;
            numberHint.setText(R.string.reg_example_number_error_1);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (numberText.length() < MIN_MOBILE_LENGTH) {
            valid = false;
            numberHint.setText(R.string.reg_example_number_error_2);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (numberText.length() > MAX_MOBILE_LENGTH) {
            valid = false;
            numberHint.setText(R.string.reg_example_number_error_3);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            numberHint.setText(R.string.reg_example_number);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isPasswordValid() {
        boolean valid;
        valid = !TextUtils.isEmpty(password.getText());
        if (!valid) {
            passwordHint.setText(R.string.reg_example_password_error_1);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            passwordHint.setText(R.string.reg_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        }
        return valid;
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
