package org.gem.indo.dooit.controllers.survey;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.SurveyManager;
import org.gem.indo.dooit.controllers.DooitBotController;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.helpers.notifications.Notifier;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotCallType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2017/01/09.
 */

abstract public class SurveyController extends DooitBotController {

    /**
     * When questions are unanswered because the survey jumped forward.
     */
    protected static final int ANSWER_NOT_APPLICABLE = 998;

    /**
     * The user chose not to answer the question, for whatever reason.
     */
    protected static final int ANSWER_MISSING = 999;

    /**
     * The user does not know what the answer is.
     */
    protected static final int ANSWER_DOES_NOT_KNOW = 888;

    protected static final int ANSWER_YES = 1;
    protected static final int ANSWER_NO = 0;

    protected static final int ANSWER_APPROVE = 1;
    protected static final int ANSWER_NEUTRAL = 2;
    protected static final int ANSWER_DISAPPROVE = 3;

    protected static final String CONSENT_KEY = "survey_consent";


    @Inject
    protected SurveyManager surveyManager;

    @Inject
    protected Persisted persisted;

    protected CoachSurvey survey;

    public SurveyController(Context context, BotType botType, CoachSurvey survey) {
        super(context, botType);
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        this.survey = survey;
    }

    @Override
    public void onAnswerInput(BotParamType inputType, Answer answer) {
        super.onAnswerInput(inputType, answer);
    }

    @Override
    public void resolveParam(BaseBotModel model, BotParamType paramType) {
        if (!hasSurvey()) {
            super.resolveParam(model, paramType);
            return;
        }

        String key = paramType.getKey();
        switch (paramType) {
            case SURVEY_TITLE:
                model.values.put(key, survey.getTitle());
                break;
            case SURVEY_INTRO:
                model.values.put(key, survey.getIntro());
                break;
            case SURVEY_OUTRO:
                model.values.put(key, survey.getOutro());
                break;
            default:
                super.resolveParam(model, paramType);
        }
    }

    @Override
    public void onAnswer(Answer answer) {
        Map<String, String> draft = createDraft(answer);
        if (!draft.isEmpty())
            if (hasSurvey())
                surveyManager.draft(survey.getId(), createDraft(answer), new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {

                    }
                }).subscribe();
    }

    @Override
    public void onCall(BotCallType key, Map<String, Answer> answerLog, BaseBotModel model) {

    }

    @Override
    public void onDone(Map<String, Answer> answerLog) {

    }

    protected abstract String[] getQuestionKeys();

    protected Map<String, String> createDraft(@NonNull Answer answer) {
        Map<String, String> draft = new HashMap<>();
        draft.put(answer.getParentName(), answer.getValue());
        return draft;
    }

    protected boolean answerEquals(Answer answer, int answerValue) {
        if (answer == null || answer.getValue() == null)
            return false;

        try {
            return answerValue == Integer.parseInt(answer.getValue());
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean hasSurvey() {
        return survey != null;
    }

    /**
     * Helper to check whether the user has given consent to take the survey.
     *
     * @param submission   The submission that the controller is building up. The consent answer
     *                     will be added to the submission under CONSENT_KEY.
     * @param ageAnswer    The answer of whether the user is old enough to give consent. If it is
     *                     null then no consent was given.
     * @param parentAnswer The answer of whether the user's parent gave consent. If it is
     *                     null then the user is old enough to consent themselves.
     */
    static void handleConsent(@NonNull Map<String, String> submission,
                              @Nullable Answer ageAnswer, @Nullable Answer parentAnswer) {

        if (ageAnswer == null) {
            // If no age answer is present, no consent was given
            submission.put(CONSENT_KEY, Long.toString(ANSWER_NO));
            return;
        }

        String ageVal = !TextUtils.isEmpty(ageAnswer.getValue()) ? ageAnswer.getValue() : Long.toString(ANSWER_NO);
        String parentVal = parentAnswer != null ? parentAnswer.getValue() : Long.toString(ANSWER_NO);

        if (ageVal.equals(Long.toString(ANSWER_NO)))
            // Users above age 17 consented via the app level Terms & Conditions
            submission.put(CONSENT_KEY, Long.toString(ANSWER_YES));
        else
            // Use parent's consent when users are below age 17
            submission.put(CONSENT_KEY, parentVal);
    }

    /**
     * Remove push notification if it's still in the phone's notification drawer.
     * <p>
     * After user has successfully submitted, they should not be able to take the
     * survey again. The server endpoint should prevent further notifications.
     */
    protected void clearNotifications() {
        //
        Context ctx = getContext();
        if (ctx != null) {
            Notifier notifier = new Notifier(ctx);
            notifier.cancel(new NotificationType[]{
                    NotificationType.SURVEY_AVAILABLE,
                    NotificationType.SURVEY_REMINDER_1,
                    NotificationType.SURVEY_REMINDER_2
            });
        }
    }
}
