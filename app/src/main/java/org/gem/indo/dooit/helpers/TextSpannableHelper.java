package org.gem.indo.dooit.helpers;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

/**
 * Created by Rudolph Jacobs on 2016-12-01.
 */

public class TextSpannableHelper {
    private String delimiter = "*";
    private String delimiterPattern = "\\*";

    public TextSpannableHelper() {
    }

    public TextSpannableHelper(String delimiter, String delimiterPattern) {
        this.delimiter = delimiter;
        this.delimiterPattern = delimiterPattern;
    }

    public Spannable styleText(Context context, @StyleRes int textAppearanceStyle, String text) {
        Spannable styledText = new SpannableString(text.replaceAll(delimiterPattern, ""));
        while (text.contains(delimiter)) {
            int start = text.indexOf(delimiter);
            text = text.replaceFirst(delimiterPattern, "");
            int end = text.indexOf(delimiter);
            text = text.replaceFirst(delimiterPattern, "");
            TextAppearanceSpan span = new TextAppearanceSpan(context, textAppearanceStyle);
            styledText.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return styledText;
    }
}
