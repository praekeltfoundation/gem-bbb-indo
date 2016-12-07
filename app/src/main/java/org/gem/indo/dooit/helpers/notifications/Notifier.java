package org.gem.indo.dooit.helpers.notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.main.MainActivity;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public class Notifier {

    private Context context;

    public Notifier(Context context) {
        this.context = context;
    }

    /**
     * @param notifyType
     * @param cls        The Activity type to open when the notification is clicked.
     */
    public <T extends Activity> void notify(NotificationType notifyType, Class<T> cls, String contentText) {
        // TODO: Activity class argument might have to be replaced with a DooitActivityBuilder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(context, R.mipmap.ic_launcher)).getBitmap())
                .setContentTitle(context.getString(notifyType.getTitleRes()))
                .setContentText(contentText)
                .setAutoCancel(true);

        // Fix old API vector drawable crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setSmallIcon(R.drawable.ic_d_notification_icon_small);
        else
            builder.setSmallIcon(R.mipmap.ic_launcher);

        // Setting up an artificial activity stack allows the notification to be clicked, open the
        // provided Activity class, and close the app when user clicks back.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NotificationType.NOTIFICATION_TYPE, notifyType.getMessageId());
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notifyType.getMessageId(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyType.getMessageId(), builder.build());
    }
}