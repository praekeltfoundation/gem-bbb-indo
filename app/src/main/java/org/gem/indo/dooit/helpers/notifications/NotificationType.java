package org.gem.indo.dooit.helpers.notifications;

import org.gem.indo.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public enum NotificationType {
    CHALLENGE_AVAILABLE(100, R.string.notification_title_challenge_available),
    // TODO: Two days after a Challenge has been published, if the user has not participated, they should be reminded
    CHALLENGE_REMINDER(101, R.string.notification_title_challenge_reminder),
    SAVING_REMINDER(200, R.string.notification_title_saving_reminder);

    // Argument key used in Intent extra for directing the MainFragment's viewpager
    public static final String NOTIFICATION_TYPE = "notification_type";
    private static Map<Integer, NotificationType> map = new HashMap<>();

    static {
        for (NotificationType notifyType : NotificationType.values())
            map.put(notifyType.getMessageId(), notifyType);
    }

    private int messageId;
    private int titleRes;

    NotificationType(int messageId, int titleResource) {
        this.messageId = messageId;
        this.titleRes = titleResource;
    }

    public static NotificationType getValueOf(int messageId) {
        return map.get(messageId);
    }

    public int getMessageId() {
        return messageId;
    }

    public int getTitleRes() {
        return titleRes;
    }
}
