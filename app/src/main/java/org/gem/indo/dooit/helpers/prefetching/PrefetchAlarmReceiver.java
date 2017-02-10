package org.gem.indo.dooit.helpers.prefetching;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Reinhardt on 2017/02/10.
 */

public class PrefetchAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            prefetchBadgeImages(context);
        }

    private void prefetchBadgeImages(Context context) {
        //TODO
    }
}