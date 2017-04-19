package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.CustomNotification;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chad Garrett on 2017/02/14.
 */

public class CustomNotificationResponse {

    private List<CustomNotification> notifications = new ArrayList<>();

    private boolean available;

    public List<CustomNotification> getNotifcations() {
        if (available) {
            return notifications;
        } else {
            return null;
        }
    }
}
