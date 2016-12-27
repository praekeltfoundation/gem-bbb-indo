package org.gem.indo.dooit.helpers.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Wimpie Victor on 2016/12/26.
 */

public class SocialSharer {

    private static final String TYPE_TEXT = "text/plain";
    private Context context;

    public SocialSharer(Context context) {
        this.context = context;
    }

    /**
     * @param title     The title at the top of the chooser.
     * @param hyperlink The link to share.
     */
    public void share(CharSequence title, Uri hyperlink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_TEXT);
        intent.putExtra(Intent.EXTRA_TEXT, hyperlink.toString());
        Intent chooser = Intent.createChooser(intent, title);
        context.startActivity(chooser);
    }
}
