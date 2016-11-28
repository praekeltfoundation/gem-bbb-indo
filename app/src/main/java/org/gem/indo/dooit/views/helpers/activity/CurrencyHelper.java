package org.gem.indo.dooit.views.helpers.activity;

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
            return NumberFormat.getCurrencyInstance().format(NumberFormat.getInstance().parse(value));
        } catch (Exception e) {
           //TODO: this should be logged to an external telemetry system
        }
        return NumberFormat.getCurrencyInstance().format(DEFAULT_VALUE);
    }

    public static String getCurrencyString() {
        return Currency.getInstance(Locale.getDefault()).getSymbol();
    }
}
