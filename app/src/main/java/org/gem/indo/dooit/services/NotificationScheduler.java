package org.gem.indo.dooit.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by chris on 2016-11-23.
 */

public class NotificationScheduler extends IntentService {
    public NotificationScheduler(){
        super("DoitService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {

            do{
                Thread.sleep(5000);

            }while(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
