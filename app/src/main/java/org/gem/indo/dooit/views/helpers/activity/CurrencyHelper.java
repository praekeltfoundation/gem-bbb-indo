package org.gem.indo.dooit.views.helpers.activity;

import java.text.NumberFormat;

/**
 * Created by wsche on 2016/11/23.
 */

public class CurrencyHelper {

    public static String format(Object o) {
        return NumberFormat.getCurrencyInstance().format(o);
    }
}
