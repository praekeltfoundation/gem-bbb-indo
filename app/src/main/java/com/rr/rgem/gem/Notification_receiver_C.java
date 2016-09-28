package com.rr.rgem.gem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

/**
 * Created by sjj98 on 9/15/2016.
 */
public class Notification_receiver_C extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        String myContentTitle = context.getResources().getString(R.string.app_name);
        String myContentText = context.getResources().getString(R.string.Chanllenge_receiver_Text);
        String myContentTicker = context.getResources().getString(R.string.app_name);


        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setContentTitle(myContentTitle);
        builder.setContentText(myContentText);
        builder.setTicker(myContentTicker);
        builder.setSmallIcon(R.drawable.clock);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        // Need to create an explicit intent for the new activity
        Intent intentOne= new Intent(context,ChallengeActivity.class);
        //Stack builder object has an artificial back stack mechanism
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ChallengeActivity.class);
        stackBuilder.addNextIntent(intentOne);
        PendingIntent pendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NMM= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NMM.notify(0,builder.build());
    }
}
