package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Chad Garrett on 2017/02/14.
 */

public class CustomNotificationResponse {
    @SerializedName("persist")
    private boolean persist;

    @SerializedName("message")
    private String message;

    @SerializedName("publish_date")
    private DateTime publishDate;

    @SerializedName("expiration_date")
    private DateTime expireDate;

    @SerializedName("icon")
    private String icon_url;

    public boolean shouldPersist() {
        return persist;
    }

    public boolean isNotificationActive() {
        DateTime currentDate = DateTime.now();

        // Checks to see if the notification is active
        return (currentDate.getMillis() >= publishDate.getMillis()) && (currentDate.getMillis() <= expireDate.getMillis());
    }

    public String getNotificationMessage() {
        return message;
    }

    public String getNotificationIcon() {
        if (!icon_url.equals(null) || icon_url != "") {
            return icon_url;
        }
        return "";
    }
}
