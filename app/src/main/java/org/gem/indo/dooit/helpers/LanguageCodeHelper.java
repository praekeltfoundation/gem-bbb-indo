package org.gem.indo.dooit.helpers;

import java.util.Locale;

/**
 * Created by Wimpie Victor on 2016/12/14.
 */

public class LanguageCodeHelper {

    /**
     * Backend framework uses "id" for Indonesian, Android uses "in".
     */
    public static String getLanguage() {
        String langCode = Locale.getDefault().getLanguage();
        if (langCode.toLowerCase().equals("in"))
            langCode = "id";
        return langCode;
    }
}
