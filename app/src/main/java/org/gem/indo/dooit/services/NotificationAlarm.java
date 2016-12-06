package org.gem.indo.dooit.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public class NotificationAlarm extends WakefulBroadcastReceiver {

    private static final String TAG = NotificationAlarm.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        startWakefulService(context, serviceIntent);
    }

    public static void setAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarm.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);

        // RTC because we don't want to wake the device up.
        // Interval is (milliseconds * seconds * minutes)
        int interval = 1000 * 10;
        manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pending);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        manager.cancel(sender);
    }
}
