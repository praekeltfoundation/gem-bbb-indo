package org.gem.indo.dooit.helpers.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/12/26.
 */

public class SocialSharer {

    public static final String TYPE_TEXT = "text/plain";
    public static final String TYPE_IMAGE_ANY = "image/*";
    private Context context;

    public SocialSharer(Context context) {
        this.context = context;
    }

    /**
     * @param title     The title at the top of the chooser.
     * @param hyperlink The link to share.
     */
    public void share(CharSequence title, Uri hyperlink) {
        query(TYPE_TEXT);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_TEXT);
        intent.putExtra(Intent.EXTRA_TEXT, hyperlink.toString());
        Intent chooser = Intent.createChooser(intent, title);
        context.startActivity(chooser);
    }

    /**
     * Queries which apps are installed that can handle the provided content.
     */
    public List<String> query(String contentType) {
        List<String> results = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(contentType);

        int flags = 0;
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, flags);
        for (ResolveInfo info : infos)
            results.add(info.activityInfo.packageName);

        return results;
    }
}
