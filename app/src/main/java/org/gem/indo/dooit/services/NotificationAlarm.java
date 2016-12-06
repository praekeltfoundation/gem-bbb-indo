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
    // milliseconds * seconds * minutes * hours * days
    public static final long WEEK = 1000 * 60 * 60 * 24 * 7;
    public static final long TEN_SECONDS = 1000 * 10; // For dev

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        startWakefulService(context, serviceIntent);
    }

    public static void setAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarm.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // RTC because we don't want to wake the device up.
        // Interval is (milliseconds * seconds * minutes)
        manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), TEN_SECONDS, pending);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(sender);
    }
}
