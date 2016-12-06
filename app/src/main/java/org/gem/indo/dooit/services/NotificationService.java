package org.gem.indo.dooit.services;

import android.app.IntentService;
import android.content.Intent;

import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.helpers.notifications.Notifier;
import org.gem.indo.dooit.views.main.MainActivity;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public class NotificationService extends IntentService {

    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // HTTP requests go here
        new Notifier(getApplicationContext())
                .notify(NotificationType.CHALLENGE_AVAILABLE, MainActivity.class, "From Alarm");
        complete(intent);
    }

    protected void complete(Intent intent) {
        // release wake lock
        NotificationAlarm.completeWakefulIntent(intent);
    }
}
