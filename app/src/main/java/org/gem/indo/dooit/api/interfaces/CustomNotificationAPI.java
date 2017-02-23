package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.CustomNotificationResponse;
import org.gem.indo.dooit.models.challenge.BaseChallenge;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Chad Garrett on 2017/02/14.
 */

public interface CustomNotificationAPI {
    @GET("/api/notifications/current/")
    Observable<CustomNotificationResponse> getCurrentCustomNotification();
}
