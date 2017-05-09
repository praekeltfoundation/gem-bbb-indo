package org.gem.indo.dooit.views.helpers.activity;

import com.crashlytics.android.Crashlytics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by wsche on 2016/11/23.
 */

public class CurrencyHelper {
    public static double DEFAULT_VALUE = 0.0f;
    private static Locale indonesia = new Locale("in", "ID");

    private static Pattern currencyFormat = Pattern.compile("^Rp[.]?\\s*(.+)$");

    public static String format(Object o) {
        try {
            // Requested currency format is 'Rp. XX.XXX,-'
            // Number formatter for Indonesian locale returns 'RpXX.XXX'
            // Regex formats currency as requested

            String indonesianFormat = NumberFormat.getCurrencyInstance(indonesia).format(Double.valueOf(String.valueOf(o)));
            indonesianFormat = indonesianFormat.replaceFirst("^Rp[.]?\\s*(.+)(,-)?$", "Rp. $1,-");
            return indonesianFormat;

//            return NumberFormat.getCurrencyInstance(indonesia).format(Double.valueOf(String.valueOf(o)));
//            return NumberFormat.getCurrencyInstance().format(Double.valueOf((String.valueOf(o))));
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

    public static char getSeperator() {
        return ((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("in", "ID"))).getDecimalFormatSymbols().getGroupingSeparator();
        //return ((DecimalFormat) NumberFormat.getCurrencyInstance()).getDecimalFormatSymbols().getGroupingSeparator();
    }
}
