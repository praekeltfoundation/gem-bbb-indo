package org.gem.indo.dooit.helpers.notifications;

import org.gem.indo.dooit.R;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public enum NotificationType {
    CHALLENGE_AVAILABLE(100, R.string.notification_title_challenge_available);


    private int messageId;
    private int titleRes;

    NotificationType(int messageId, int titleResource) {
        this.messageId = messageId;
        this.titleRes = titleResource;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getTitleRes() {
        return titleRes;
    }
}
