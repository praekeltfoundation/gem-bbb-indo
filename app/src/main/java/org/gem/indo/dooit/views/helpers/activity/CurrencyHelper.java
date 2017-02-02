package org.gem.indo.dooit.views.helpers.activity;

import android.app.Application;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by wsche on 2016/11/23.
 */

public class CurrencyHelper {
    public static double DEFAULT_VALUE = 0.0f;
    private static Locale indonesia = new Locale("in", "ID");

    public static String format(Object o) {
        try {
            // Lock currency symbols to Indonesian Rupiah
            return NumberFormat.getCurrencyInstance(indonesia).format(Double.valueOf(String.valueOf(o)));
            //return NumberFormat.getCurrencyInstance().format(Double.valueOf((String.valueOf(o))));
        } catch (Exception e) {
            Crashlytics.log("Error converting number to currency [" + String.valueOf(o) + "]");
            Crashlytics.logException(e);
        }
        return NumberFormat.getCurrencyInstance().format(DEFAULT_VALUE);
    }

    public static String getCurrencySymbol() {
        // Lock currency symbols to Indonesian Rupiah
        return Currency.getInstance(indonesia).getSymbol(indonesia);
        //return Currency.getInstance(Locale.getDefault()).getSymbol();
    }
}
