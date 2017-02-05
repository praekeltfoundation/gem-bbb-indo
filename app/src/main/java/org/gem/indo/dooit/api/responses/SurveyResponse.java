package org.gem.indo.dooit.api.responses;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class SurveyResponse {

    /**
     * The number of days since the survey is available for selecting reminders.
     */
    private static final int REMINDER_THRESHOLD_1 = 3;
    private static final int REMINDER_THRESHOLD_2 = 7;

    private boolean available;
    @SerializedName("inactivity_age")
    private int inactivtyAge;
    private CoachSurvey survey;

    /////////////
    // Getters //
    /////////////

    public boolean isAvailable() {
        return available;
    }

    public Integer getInactivtyAge() {
        return inactivtyAge;
    }

    public CoachSurvey getSurvey() {
        return survey;
    }

    public boolean hasSurvey() {
        return available && survey != null;
    }

    /////////////
    // Methods //
    /////////////

    @NonNull
    public NotificationType getNotificationType() {
        if (inactivtyAge >= REMINDER_THRESHOLD_2)
            return NotificationType.SURVEY_REMINDER_2;
        else if (inactivtyAge >= REMINDER_THRESHOLD_1)
            return NotificationType.SURVEY_REMINDER_1;
        else
            return NotificationType.SURVEY_AVAILABLE;
    }
}
