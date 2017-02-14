package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.CustomNotificationAPI;
import org.gem.indo.dooit.api.responses.CustomNotificationResponse;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Chad Garrett on 2017/02/14.
 */

public class CustomNotificationManager extends DooitManager {

    private final CustomNotificationAPI customNotificationAPI;

    @Inject
    public CustomNotificationManager(Application application) {
        super(application);
        customNotificationAPI = retrofit.create(CustomNotificationAPI.class);
    }

    public Observable<CustomNotificationResponse> fetchCustomNotification(DooitErrorHandler errorHandler){
        return useNetwork(customNotificationAPI.getCurrentCustomNotification(), errorHandler);
    }
}
