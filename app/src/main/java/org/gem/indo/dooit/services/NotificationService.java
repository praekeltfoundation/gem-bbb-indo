package org.gem.indo.dooit.services;

import android.app.IntentService;
import android.content.Intent;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.helpers.notifications.Notifier;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

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
    ChallengeManager challengeManager;

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

        requests.add(challengeManager.retrieveCurrentChallenge(true, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }));

        Observable.from(requests).flatMap(new Func1<Observable<?>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<?> observable) {
                return observable;
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                complete(intent);
            }
        }).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof BaseChallenge)
                    currentChallengeRetrieved((BaseChallenge) o);
            }
        });
    }

    protected void currentChallengeRetrieved(BaseChallenge challenge) {
        new Notifier(getApplicationContext())
                .notify(NotificationType.CHALLENGE_AVAILABLE, MainActivity.class, challenge.getName());
    }

    protected void complete(Intent intent) {
        // release wake lock
        NotificationAlarm.completeWakefulIntent(intent);
    }
}
