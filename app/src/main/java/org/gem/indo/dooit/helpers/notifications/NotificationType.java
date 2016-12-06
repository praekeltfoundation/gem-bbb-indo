package org.gem.indo.dooit.helpers.notifications;

import org.gem.indo.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public enum NotificationType {
    CHALLENGE_AVAILABLE(100, R.string.notification_title_challenge_available);

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
        if (!map.containsKey(messageId))
            return null;
        return map.get(messageId);
    }

    public int getMessageId() {
        return messageId;
    }

    public int getTitleRes() {
        return titleRes;
    }
}
