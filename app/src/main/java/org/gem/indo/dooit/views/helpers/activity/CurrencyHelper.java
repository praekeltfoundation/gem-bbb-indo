package org.gem.indo.dooit.views.helpers.activity;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by wsche on 2016/11/23.
 */

public class CurrencyHelper {

    public static String format(Object o) {
        return NumberFormat.getCurrencyInstance().format(Float.parseFloat(String.valueOf(o)));
    }

    public static String getCurrencyString() {
        return Currency.getInstance(Locale.getDefault()).getSymbol();
    }
}
