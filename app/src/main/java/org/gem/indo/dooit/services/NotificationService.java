package org.gem.indo.dooit.services;

import android.app.IntentService;
import android.content.Intent;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AchievementManager;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.api.managers.SurveyManager;
import org.gem.indo.dooit.api.responses.AchievementResponse;
import org.gem.indo.dooit.api.responses.SurveyResponse;
import org.gem.indo.dooit.api.responses.WinnerResponse;
import org.gem.indo.dooit.helpers.DooitParamBuilder;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.helpers.notifications.Notifier;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.survey.CoachSurvey;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getName();

    @Inject
    AchievementManager achievementManager;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    SurveyManager surveyManager;

    @Inject
    Persisted persisted;

    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((DooitApplication) getApplication()).component.inject(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        List<Observable<?>> requests = new ArrayList<>();

        if (persisted.shouldNotify(NotificationType.CHALLENGE_AVAILABLE) ||
                persisted.shouldNotify(NotificationType.CHALLENGE_REMINDER))
            requests.add(challengeManager.retrieveCurrentChallenge(true, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }));

        if (persisted.shouldNotify(NotificationType.CHALLENGE_WINNER)) {
            requests.add(challengeManager.fetchChallengeWinner(new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }));
        }

        if (persisted.shouldNotify(NotificationType.SAVING_REMINDER)) {
            User user = persisted.getCurrentUser();
            if (user != null)
                requests.add(achievementManager.retrieveAchievement(user.getId(), new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {

                    }
                }));
        }

        if (persisted.shouldNotify(NotificationType.SURVEY_AVAILABLE))
            requests.add(surveyManager.retrieveCurrentSurvey(new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }));


        if (requests.size() > 0)
            // Using flatmap to perform requests serially
            Observable.from(requests).flatMap(new Func1<Observable<?>, Observable<?>>() {
                @Override
                public Observable<?> call(Observable<?> observable) {
                    return observable;
                }
            }).doAfterTerminate(new Action0() {
                @Override
                public void call() {
                    complete(intent);
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if (o instanceof BaseChallenge)
                        currentChallengeRetrieved((BaseChallenge) o);
                    else if (o instanceof AchievementResponse)
                        achievementsRetrieved((AchievementResponse) o);
                    else if (o instanceof SurveyResponse)
                        surveyRetrieved((SurveyResponse) o);
                    else if (o instanceof WinnerResponse)
                        winnerRetrieved((WinnerResponse) o);
                }
            });
        else
            // Nothing was requested. All notifications turned off in Settings.
            complete(intent);
    }

    protected void currentChallengeRetrieved(BaseChallenge challenge) {
        // TODO: Logic to distinguish between a new Challenge available, and reminding the user to take a Challenge
        if (persisted.shouldNotify(NotificationType.CHALLENGE_AVAILABLE)
                && persisted.getCurrentUser() != null) {

            Map<String, Object> params = DooitParamBuilder.create(this)
                    .setUser(persisted.getCurrentUser())
                    .build();

            ParamMatch match = ParamParser.parse(getString(R.string.notification_content_challenge_available));

            new Notifier(getApplicationContext())
                    .notify(NotificationType.CHALLENGE_AVAILABLE, MainActivity.class,
                            match.process(params));
        }
    }

    protected void achievementsRetrieved(AchievementResponse response) {
        if (persisted.shouldNotify(NotificationType.SAVING_REMINDER) && response.shouldRemindSavings()) {

            Map<String, Object> params = DooitParamBuilder.create(this)
                    .setAchievements(response)
                    .setUser(persisted.getCurrentUser())
                    .build();

            ParamMatch match = ParamParser.parse(getApplicationContext()
                    .getString(R.string.notification_content_saving_reminder));

            new Notifier(getApplicationContext()).notify(
                    NotificationType.SAVING_REMINDER,
                    MainActivity.class,
                    match.process(params)
            );
        }
    }

    protected void surveyRetrieved(SurveyResponse response) {
        if (response.hasSurvey() && persisted.getCurrentUser() != null) {
            CoachSurvey survey = response.getSurvey();

            Map<String, String> extras = new HashMap<>();
            extras.put(NotificationArgs.SURVEY_ID, Long.toString(survey.getId()));

            Map<String, Object> params = DooitParamBuilder.create(this)
                    .setUser(persisted.getCurrentUser())
                    .build();

            NotificationType notifyType = response.getNotificationType();
            ParamMatch match = null;
            switch (notifyType) {
                case SURVEY_AVAILABLE:
                    match = ParamParser.parse(getString(R.string.notification_content_survey_available));
                    break;
                case SURVEY_REMINDER_1:
                    match = ParamParser.parse(getString(R.string.notification_content_survey_reminder_1));
                    break;
                case SURVEY_REMINDER_2:
                    match = ParamParser.parse(getString(R.string.notification_content_survey_reminder_2));
            }

            String content = match != null ? match.process(params) : getString(notifyType.getTitleRes());

            // Specifically for BASELINE and EATOOL types so bot knows what conversation to start
            if (survey.hasBotType())
                extras.put(NotificationArgs.SURVEY_TYPE, survey.getBotType().name());

            new Notifier(getApplicationContext()).notify(
                    notifyType,
                    MainActivity.class,
                    content,
                    extras
            );

            if (survey.hasBotType())
                // Save Survey so Bot will start quicker
                persisted.saveConvoSurvey(survey.getBotType(), survey);
        }
    }

    protected void winnerRetrieved(WinnerResponse response) {
        if (persisted.shouldNotify(NotificationType.CHALLENGE_WINNER)
                && persisted.getCurrentUser() != null) {
            if (response.isAvailable()) {

                Map<String, Object> params = DooitParamBuilder.create(this)
                        .setUser(persisted.getCurrentUser())
                        .build();

                ParamMatch match = ParamParser.parse(getString(R.string.notification_content_challenge_winner));

                new Notifier(getApplicationContext())
                        .notify(NotificationType.CHALLENGE_WINNER, MainActivity.class,
                                match.process(params));
                persisted.saveConvoWinner(BotType.CHALLENGE_WINNER, response.getBadge(), response.getChallenge());
            }
        }
    }

    protected void complete(Intent intent) {
        // release wake lock
        NotificationAlarm.completeWakefulIntent(intent);
    }
}
