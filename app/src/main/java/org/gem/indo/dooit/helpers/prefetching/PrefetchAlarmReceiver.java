package org.gem.indo.dooit.helpers.prefetching;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BadgeManager;
import org.gem.indo.dooit.api.responses.AuthenticationResponse;
import org.gem.indo.dooit.api.responses.BadgeImageUrlsResponse;
import org.gem.indo.dooit.helpers.images.DraweeHelper;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Reinhardt on 2017/02/10.
 */

public class PrefetchAlarmReceiver extends BroadcastReceiver {

    @Inject
    BadgeManager badgeManager;

    private String [] imageUrls;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        prefetchBadgeImages(context);
    }

    private void prefetchBadgeImages(Context context) {
        badgeManager.getBadgeUrls(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<BadgeImageUrlsResponse>() {
            @Override
            public void call(BadgeImageUrlsResponse response) {
                imageUrls = response.urls;
                new Thread(new FetchImagesThread()).start();
            }
        });
    }

    private class FetchImagesThread implements Runnable {
        @Override
        public void run() {
            for (String url : imageUrls) {
                DraweeHelper.cacheImage(Uri.parse(url));
            }
        }
    }
}