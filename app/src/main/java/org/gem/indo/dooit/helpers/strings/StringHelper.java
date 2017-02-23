package org.gem.indo.dooit.helpers.strings;


import android.support.annotation.Nullable;

/**
 * Created by Reinhardt on 2017/02/01.
 */

public class StringHelper {

    public static String newString(String string) {
        if (string == null) {
            return null;
        } else {
            return new String(string);
        }
    }

    /**
     * Returns true if string is null or has length of 0. Does the same as
     * {@link android.text.TextUtils}, but is used so classes can be unit tested.
     *
     * @param str The string to be examined
     * @return true if string is null or 0 length
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }
}
