package org.gem.indo.dooit.views.helpers.activity;

import com.crashlytics.android.Crashlytics;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by wsche on 2016/11/23.
 */

public class CurrencyHelper {
    public static double DEFAULT_VALUE = 0.0f;
    public static String format(Object o) {
        try {
            String value = String.valueOf(o);
            BigDecimal number = new BigDecimal(value);
            return NumberFormat.getCurrencyInstance().format(number);
        } catch (Exception e) {
            Crashlytics.log("Error converting number to currency [" + String.valueOf(o) + "]");
            Crashlytics.logException(e);
        }
        return NumberFormat.getCurrencyInstance().format(DEFAULT_VALUE);
    }

    public static String getCurrencySymbol() {
        return Currency.getInstance(Locale.getDefault()).getSymbol();
    }
}
