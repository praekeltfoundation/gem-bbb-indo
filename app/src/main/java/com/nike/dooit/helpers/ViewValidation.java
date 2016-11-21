package com.nike.dooit.helpers;

import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;

import com.nike.dooit.R;

import java.util.regex.Pattern;

/**
 * Created by chris on 2016-11-21.
 */

public class ViewValidation {
    public static int MIN_PASSWORD = 6;
    private static final String NAME_PATTERN = "[a-zA-Z0-9@\\.\\=\\-\\_]+";
    private static final int MAX_NAME_LENGTH = 150;

    public static class Result{
        protected Result(int message, boolean valid){
            this.message = message;
            this.valid = valid;
        }
        public final int message;
        public final boolean valid;
    };

    public static Result isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            return new Result(R.string.reg_example_password_error_1,false);
        } else if (password.length() < MIN_PASSWORD) {
            return new Result(R.string.reg_example_password_error_2,false);
        }
        return new Result(0,true);

    }
    public static Result isNameValid(String name) {
        String nameText = name.toString();
        if (TextUtils.isEmpty(nameText)) {
            return new Result(R.string.reg_example_name_error_1,false);
        } else if (nameText.contains(" ")) {
            return new Result(R.string.reg_example_name_error_2,false);
        } else if (!Pattern.compile(NAME_PATTERN).matcher(nameText).matches()) {
            return new Result(R.string.reg_example_name_error_3,false);
        } else if (nameText.length() > MAX_NAME_LENGTH) {
            return new Result(R.string.reg_example_name_error_4,false);
        }
        return new Result(R.string.reg_example_name,true);
    }

}
