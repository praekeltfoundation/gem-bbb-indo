package org.gem.indo.dooit.helpers.prefetching;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.BadgeManager;

import javax.inject.Inject;

/**
 * Created by Reinhardt on 2017/02/10.
 */

public class PrefetchAlarmReceiver extends BroadcastReceiver {

    @Inject
    BadgeManager badgeManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        prefetchBadgeImages(context);
    }

    private void prefetchBadgeImages(Context context) {
        //TODO
    }
}