package org.gem.indo.dooit.api.responses;

import org.gem.indo.dooit.models.survey.CoachSurvey;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class SurveyResponse {

    private boolean available;
    private CoachSurvey survey;

    public boolean isAvailable() {
        return available;
    }

    public CoachSurvey getSurvey() {
        return survey;
    }
}
