package org.gem.indo.dooit.helpers.notifications;

import org.gem.indo.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public enum NotificationType {
    CHALLENGE_AVAILABLE(100, R.string.notification_title_challenge_available),
    CHALLENGE_REMINDER(101, R.string.notification_title_challenge_reminder), // TODO: Two days after a Challenge has been published, if the user has not participated, they should be reminded
    CHALLENGE_ANSWER_REMINDER(103, R.string.notification_title_challenge_answer_reminder), // TODO: Remind the user that they have an answer persisted that has not been submitted
    CHALLENGE_WINNER(104,R.string.notification_title_challenge_winner),
    GOAL_DEADLINE_MISSED(200, R.string.notification_title_goal_deadline_missed), // TODO: Notify user that they missed one of their Goals
    SAVING_REMINDER(300, R.string.notification_title_saving_reminder),
    SURVEY_AVAILABLE(400, R.string.notification_title_survey_available),
    SURVEY_REMINDER_1(401, R.string.notification_title_survey_reminder),
    SURVEY_REMINDER_2(402, R.string.notification_title_survey_reminder),
    AD_HOC(500, R.string.notification_title_ad_hoc); // TODO: Ad Hoc Notifications from CMS

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
