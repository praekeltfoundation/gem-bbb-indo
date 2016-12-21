package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AuthenticationManager;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.api.responses.OnboardingResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.TextSpannableHelper;
import org.gem.indo.dooit.models.Profile;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.web.MinimalWebViewActivity;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.functions.Action0;
import rx.functions.Action1;

public class RegistrationActivity extends DooitActivity {

    private static final int DEFAULT_AGE = 16;

    private static final int MIN_AGE = 12;
    private static final int MAX_AGE = 80;
    private static final String NAME_PATTERN = "[a-zA-Z0-9@\\.\\=\\-\\_]+";
    private static final String MOBILE_PATTERN = "^\\+?1?\\d{9,15}$";
    private static final int MAX_NAME_LENGTH = 150;
    private static final int MIN_MOBILE_LENGTH = 9;
    private static final int MAX_MOBILE_LENGTH = 16;
    private static final int MAX_PASSWORD = 6;

    @BindView(R.id.activity_registration)
    View background;

    @BindView(org.gem.indo.dooit.R.id.activity_registration_t_c_text_view)
    TextView textViewTC;

    @BindView(R.id.activity_registration_login_text_view)
    TextView textViewLogin;

    @BindView(R.id.activity_registration_name_text_edit)
    EditText name;

    @BindView(R.id.activity_registration_email_text_edit)
    EditText email;

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

    @BindView(R.id.activity_registration_email_example_text_edit)
    TextView emailHint;

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
        setContentView(org.gem.indo.dooit.R.layout.activity_registration);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        
        String stringTc = getResources().getString(org.gem.indo.dooit.R.string.reg_t_c);
        TextSpannableHelper spanTcHelper = new TextSpannableHelper();

        String stringLogin = getResources().getString(org.gem.indo.dooit.R.string.already_registered_log_in);
        TextSpannableHelper spanLoginHelper = new TextSpannableHelper();

        // Age
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter(this, org.gem.indo.dooit.R.layout.item_spinner_age);
        for (int i = MIN_AGE; i <= MAX_AGE; i++)
            ageAdapter.add(i);
        age.setAdapter(ageAdapter);

        // Default gender
        gender.check(R.id.activity_registration_gender_girl);
        
        textViewTC.setText(spanTcHelper.styleText(this, R.style.AppTheme_TextView_Bold_Small_Accented, stringTc));
        textViewLogin.setText(spanLoginHelper.styleText(this, R.style.AppTheme_TextView_Bold_Small_Accented, stringLogin));
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
        password.setImeActionLabel(getString(R.string.label_register), EditorInfo.IME_ACTION_DONE);
        age.setSelection(DEFAULT_AGE - MIN_AGE); // 16

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    textView.clearFocus();
                    age.requestFocus();
                    age.performClick();
                }
                return true;
            }
        });
    }

    @OnClick({R.id.activity_registration_gender_girl, R.id.activity_registration_gender_boy,})
    public void genderClick() {
        number.requestFocus();
        number.performClick();
    }


    @OnClick(R.id.activity_registration_t_c_text_view)
    public void openTC() {
        MinimalWebViewActivity.Builder.create(this)
                //.setTitle(getString(org.gem.indo.dooit.R.string.title_activity_terms_and_conditions))
                .setUrl(Constants.TERMS_URL)
                .startActivity();
    }

    @OnEditorAction(R.id.activity_registration_password_edit_text)
    boolean imeLogin(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            register();
        }
        return true;
    }

    @OnClick(R.id.activity_registration_register_button)
    public void register() {
        if (!detailsValid()) {
            hideKeyboard();
            return;
        }

        hideKeyboard();
        showProgressDialog(R.string.reg_progress_dialog_message);
        authenticationManager.onboard(getUser(), new DooitErrorHandler() {
            @Override
            public void onError(final DooitAPIError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error != null && error.getErrorResponse() != null) {
                            if (error.getErrorResponse().getFieldErrors().containsKey("username")) {
                                Snackbar.make(registerButton, R.string.reg_duplicate_username_error, Snackbar.LENGTH_LONG).show();
                                nameHint.setText(R.string.reg_duplicate_username_error);
                                nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
                            } else {
                                Snackbar.make(registerButton, R.string.general_error, Snackbar.LENGTH_LONG).show();
                            }
                        } else if (error.getCause() instanceof SocketTimeoutException) {
                            Snackbar.make(registerButton, R.string.connection_timed_out, Snackbar.LENGTH_LONG).show();
                        }
                        dismissDialog();
                    }
                });
            }
        }).subscribe(new Action1<OnboardingResponse>() {
            @Override
            public void call(OnboardingResponse onboardingResponse) {
                authenticationManager.login(getUser().getUsername(), getUser().getPassword(), new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {
                        for (String msg : error.getErrorMessages())
                            Snackbar.make(registerButton, msg, Snackbar.LENGTH_LONG).show();
                        dismissDialog();
                    }
                }).doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        dismissDialog();
                    }
                }).subscribe(new Action1<AuthenticationResponse>() {
                    @Override
                    public void call(AuthenticationResponse authenticationResponse) {
                        persisted.setCurrentUser(authenticationResponse.getUser());
                        persisted.saveToken(authenticationResponse.getToken());
                        persisted.setNewBotUser(true);
                        NotificationAlarm.setAlarm(RegistrationActivity.this);
                        ProfileImageActivity.Builder.create(RegistrationActivity.this).startActivityClearTop();
                    }
                });

            }
        });
    }

    private boolean detailsValid() {
        int checkOptions = choseEmailOnly();

        switch (checkOptions){
            case 1: // Both was used
                return isNameValid() & isEmailValid() & isAgeValid() & isNumberValid() & isPasswordValid();
            case 2: // Only number
                return isNameValid() & isAgeValid() & isNumberValid() & isPasswordValid();
            case 3: // Only email
                return isNameValid() & isEmailValid() & isAgeValid() & isPasswordValid();
            default:
                return false;
        }
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

        if ((number.getText().toString() != "") && (email.getText().toString() != "")){
            profile.setMobile(number.getText().toString());
            profile.setEmail(email.getText().toString());
        }
        else if(number.getText().toString() != ""){
            profile.setMobile(number.getText().toString());
        }
        else if (email.getText().toString() != ""){
            profile.setEmail(email.getText().toString());
        }

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
            nameHint.setText(org.gem.indo.dooit.R.string.reg_example_name_error_1);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (nameText.contains(" ")) {
            valid = false;
            nameHint.setText(org.gem.indo.dooit.R.string.reg_example_name_error_2);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (!Pattern.compile(NAME_PATTERN).matcher(nameText).matches()) {
            valid = false;
            nameHint.setText(org.gem.indo.dooit.R.string.reg_example_name_error_3);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (nameText.length() > MAX_NAME_LENGTH) {
            valid = false;
            nameHint.setText(org.gem.indo.dooit.R.string.reg_example_name_error_4);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            nameHint.setText(org.gem.indo.dooit.R.string.reg_example_name);
            nameHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isEmailValid(){
        boolean valid = true;
        String emailText = email.getText().toString();
        if (TextUtils.isEmpty(emailText)) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_1);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (emailText.contains(" ")) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_2);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_3);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (emailText.length() > MAX_NAME_LENGTH) {
            valid = false;
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email_error_4);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            emailHint.setText(org.gem.indo.dooit.R.string.reg_example_email);
            emailHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;

    }

    public boolean isAgeValid() {
        boolean valid;
        valid = true;
        if (!valid) {
            ageHint.setText(org.gem.indo.dooit.R.string.reg_example_age_error_1);
            ageHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            ageHint.setText(org.gem.indo.dooit.R.string.reg_example_age);
            ageHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isNumberValid() {
        boolean valid = true;
        String numberText = number.getText().toString();
        if (TextUtils.isEmpty(numberText)) {
            valid = false;
            numberHint.setText(org.gem.indo.dooit.R.string.reg_example_number_error_1);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (numberText.length() < MIN_MOBILE_LENGTH) {
            valid = false;
            numberHint.setText(org.gem.indo.dooit.R.string.reg_example_number_error_2);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (numberText.length() > MAX_MOBILE_LENGTH) {
            valid = false;
            numberHint.setText(org.gem.indo.dooit.R.string.reg_example_number_error_3);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            numberHint.setText(org.gem.indo.dooit.R.string.reg_example_number);
            numberHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public boolean isPasswordValid() {
        boolean valid = true;
        if (TextUtils.isEmpty(password.getText())) {
            valid = false;
            passwordHint.setText(org.gem.indo.dooit.R.string.reg_example_password_error_1);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else if (password.getText().length() < MAX_PASSWORD) {
            valid = false;
            passwordHint.setText(org.gem.indo.dooit.R.string.reg_example_password_error_2);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_light, getTheme()));
        } else {
            passwordHint.setText(org.gem.indo.dooit.R.string.reg_example_password);
            passwordHint.setTextColor(ResourcesCompat.getColor(getResources(), org.gem.indo.dooit.R.color.white, getTheme()));
        }
        return valid;
    }

    public int choseEmailOnly(){
        if((number.length()!= 0) && (email.length() != 0)){
            return 1; // Both were chosen
        }
        else if (email.length()==0){
            return 2; // They only used their number
        }
        else if (number.length()==0){
            return 3; // They only used their email address
        }
        else
            return 4;
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
