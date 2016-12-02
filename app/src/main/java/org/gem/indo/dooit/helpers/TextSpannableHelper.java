package org.gem.indo.dooit.helpers;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

/**
 * Created by Rudolph Jacobs on 2016-12-01.
 */

public class TextSpannableHelper {
    private static String defaultDelimiter = "*";

    private String startDelimiter = defaultDelimiter;
    private String endDelimiter = defaultDelimiter;

    public TextSpannableHelper() {
    }

    /**
     * Sets the <code>String</code> to be used as delimiter for start of a styled block.
     *
     * @param startDelimiter  Start delimiter.
     */
    public void setStartDelimiter(String startDelimiter) {
        this.startDelimiter = startDelimiter;
    }

    /**
     * Sets the <code>String</code> to be used as delimiter for end of a styled block.
     *
     * @param endDelimiter  End delimiter.
     */
    public void setEndDelimiter(String endDelimiter) {
        this.endDelimiter = endDelimiter;
    }

    /**
     * Sets the <code>String</code> to be used as delimiter for both beginning and end of a styled block.
     *
     * @param delimiter  Delimiter for both end and start.
     */
    public void setDelimiters(String delimiter) {
        this.startDelimiter = delimiter;
        this.endDelimiter = delimiter;
    }

    /**
     * Sets the <code>String</code> to be used as delimiter for both beginning and end of
     * a styled block.
     *
     * @param startDelimiter  Delimiter for start.
     * @param endDelimiter    Delimiter for end.
     */
    public void setDelimiters(String startDelimiter, String endDelimiter) {
        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
    }

    /**
     * Styles text using <code>"*"</code> as delimiter for both start and end.
     * <p>
     * If text is going to be styled multiple times, consider instantiating an instance and using
     * its methods.
     *
     * @param context               The Activity context.
     * @param textAppearanceStyle   The style res ID to apply to the delimited blocks.
     * @param text                  The delimited text to be styled.
     * @return                      The styled text.
     * @see                         #TextSpannableHelper()
     */
    public static Spannable defStyleText(Context context, @StyleRes int textAppearanceStyle, String text) {
        TextSpannableHelper helper = new TextSpannableHelper();
        return helper.styleText(context, textAppearanceStyle, text);
    }

    /**
     * Styles text using optionally selected start and end delimiters. If not selected, "*"
     * is used for both start and end delimiter.
     *
     * @param context               The Activity context.
     * @param textAppearanceStyle   The style res ID to apply to the delimited blocks.
     * @param text                  The delimited text to be styled.
     * @return                      The styled text.
     * @see                         #setStartDelimiter(String)
     * @see                         #setEndDelimiter(String)
     * @see                         #setDelimiters(String)
     * @see                         #setDelimiters(String, String)
     */
    public Spannable styleText(Context context, @StyleRes int textAppearanceStyle, String text) {
        SpannableStringBuilder styledText = new SpannableStringBuilder();

        if (TextUtils.isEmpty(text)) {
            text = "";
        }

        if (!text.contains(startDelimiter)) {
            styledText.append(text);
            return styledText;
        }

        styledText.append(text.substring(0, text.indexOf(startDelimiter)));

        int i = 0;
        while (text.indexOf(startDelimiter, i) >= 0) {
            int start = text.indexOf(startDelimiter, i) + startDelimiter.length();
            int end = text.indexOf(endDelimiter, start);

            if (end < 0) {
                throw new IndexOutOfBoundsException("Text styling delimiter block left open.");
            }

            TextAppearanceSpan span = new TextAppearanceSpan(context, textAppearanceStyle);
            styledText.append(text.substring(start, end));
            styledText.setSpan(
                    span,
                    styledText.length() + start - end,  // len - (end - start)
                    styledText.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );

            i = end + endDelimiter.length();
        }

        styledText.append(text.substring(i));

        return styledText;
    }
}
