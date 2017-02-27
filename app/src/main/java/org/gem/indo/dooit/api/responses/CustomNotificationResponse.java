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
    @SerializedName("data")
    private List<CustomNotification> notifications = new ArrayList<>();

    @SerializedName("available")
    private boolean available;


    public List<CustomNotification> getNotifcations() {
        if (available) {
            return notifications;
        } else {
            return null;
        }
    }

//    @SerializedName("message")
//    private String message;
//
//    @SerializedName("publish_date")
//    private DateTime publishDate;
//
//    @SerializedName("expiration_date")
//    private DateTime expireDate;
//
//    @SerializedName("icon")
//    private String icon_url;
//
//    public List<CustomNotification> getNotifications() {
//        return badges;
//    }
//
//    public boolean isNotificationActive() {
//        DateTime currentDate = DateTime.now();
//
//        // Checks to see if the notification is active
//        return (currentDate.getMillis() >= publishDate.getMillis()) && (currentDate.getMillis() <= expireDate.getMillis());
//    }
//
//    public String getNotificationMessage() {
//        return message;
//    }
//
//    public String getNotificationIcon() {
//        if (!icon_url.equals(null) || icon_url != "") {
//            return icon_url;
//        }
//        return "";
//    }
}
