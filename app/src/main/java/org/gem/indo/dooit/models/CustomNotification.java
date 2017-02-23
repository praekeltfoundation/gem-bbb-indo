package org.gem.indo.dooit.models;

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

/**
 * Created by Chad Garrett on 2017/02/20.
 */

public class CustomNotification {
    @SerializedName("message")
    private String message;

    @SerializedName("publish_date")
    private DateTime publishDate;

    @SerializedName("expiration_date")
    private DateTime expireDate;

    @SerializedName("icon")
    private String icon_url;

    public boolean isNotificationActive() {
        DateTime currentDate = DateTime.now();

        // Checks to see if the notification is active
        return (currentDate.getMillis() >= publishDate.getMillis()) && (currentDate.getMillis() <= expireDate.getMillis());
    }

    public String getNotificationMessage() {
        return message;
    }

    public String getNotificationIcon() {
        return icon_url;
    }
}
