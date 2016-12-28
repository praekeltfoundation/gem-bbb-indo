package org.gem.indo.dooit.validatior;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by frede on 2016/12/28.
 */

public class ProfileValidator extends Validator {
    private static final int MIN_MOBILE_LENGTH = 9;
    private static final int MAX_MOBILE_LENGTH = 16;
    private static final String MOBILE_PATTERN = "^\\+?1?\\d{9,15}$";
    private static int responseText;

    protected ProfileValidator(){

    }

    public static boolean isAgeValid() {
        boolean valid;
        valid = true;
        if (!valid) {
            responseText = org.gem.indo.dooit.R.string.reg_example_age_error_1;
        } else {
            responseText = org.gem.indo.dooit.R.string.reg_example_age;
        }
        return valid;
    }

    public static boolean isMobileNumberValid(String numberText) {
        boolean valid = true;
        if (TextUtils.isEmpty(numberText)) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_number_error_1;
        } else if (numberText.length() < MIN_MOBILE_LENGTH) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_number_error_2;
        } else if (numberText.length() > MAX_MOBILE_LENGTH) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_number_error_3;
        } else if (!Pattern.compile(MOBILE_PATTERN).matcher(numberText).matches()) {
            valid = false;
            responseText = org.gem.indo.dooit.R.string.reg_example_number_error_4;
        } else {
            responseText = org.gem.indo.dooit.R.string.reg_example_number;
        }
        return valid;
    }

    public static int getResponseText() {
        return responseText;
    }
}
