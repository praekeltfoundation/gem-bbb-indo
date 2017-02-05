package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.models.survey.CoachSurvey;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class SurveyResponse {

    private boolean available;
    @SerializedName("inactivity_age")
    private int inactivtyAge;
    private CoachSurvey survey;

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
}
