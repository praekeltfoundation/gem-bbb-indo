package org.gem.indo.dooit.controllers.survey;

import android.content.Context;

import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

/**
 * Created by Wimpie Victor on 2017/01/19.
 */

public class EAToolSurveyController extends SurveyController {

    public EAToolSurveyController(Context context, CoachSurvey survey) {
        super(context, BotType.SURVEY_EATOOL, survey);
    }
}
