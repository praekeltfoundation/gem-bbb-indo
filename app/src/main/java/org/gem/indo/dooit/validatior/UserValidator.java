package org.gem.indo.dooit.validatior;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by frede on 2016/12/28.
 */

public class UserValidator extends Validator{

    private static final String NAME_PATTERN = "[a-zA-Z0-9@\\.\\=\\-\\_]+";
    private static final int MAX_NAME_LENGTH = 150;
    private static final int MAX_PASSWORD = 6;

    public UserValidator(){}

    public boolean isNameValid(String name) {
        valid = true;

        if (TextUtils.isEmpty(name)) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_name_error_1;
        } else if (name.contains(" ")) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_name_error_2;
        } else if (!Pattern.compile(NAME_PATTERN).matcher(name).matches()) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_name_error_3;
        } else if (name.length() > MAX_NAME_LENGTH) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_name_error_4;
        } else {
            responseText = org.gem.indo.dooit.R.string.reg_example_name;
        }
        return valid;
    }

    public boolean isEmailValid(String emailText){
        valid = true;
        if (TextUtils.isEmpty(emailText)) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_email_error_1;
        } else if (emailText.contains(" ")) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_email_error_2;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_email_error_3;
        } else if (emailText.length() > MAX_NAME_LENGTH) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_email_error_4;
        } else {
            responseText = org.gem.indo.dooit.R.string.reg_example_email;
        }
        return valid;
    }

    public boolean isPasswordValid(String password) {
        valid = true;
        if (TextUtils.isEmpty(password)){
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_password_error_1;
        } else if (password.length() < MAX_PASSWORD) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_password_error_2;
        } else {
            responseText = org.gem.indo.dooit.R.string.reg_example_password;
        }
        return valid;
    }

    public int getResponseText() {
        return responseText;
    }
}
