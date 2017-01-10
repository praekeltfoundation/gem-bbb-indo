package org.gem.indo.dooit.models.survey;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.enums.BotType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class CoachSurvey {

    private long id;
    private String title;
    private String intro;
    private String outro;
    @SerializedName("notification_body")
    private String notificationBody;
    @SerializedName("reminder_notification_body")
    private String reminderNotificationBody;

    /**
     * Used to indicate what Bot conversation to be used for this survey. To support hard coded MVP
     * Bot conversations along with dynamic Ad Hoc surveys.
     */
    @SerializedName("bot_conversation")
    private BotType botType;
    @SerializedName("form_fields")
    private List<CoachSurveyField> fields = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIntro() {
        return intro;
    }

    public String getOutro() {
        return outro;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public String getReminderNotificationBody() {
        return reminderNotificationBody;
    }

    public BotType getBotType() {
        return botType;
    }

    public List<CoachSurveyField> getFields() {
        return fields;
    }
}
