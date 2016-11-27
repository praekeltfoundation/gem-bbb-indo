package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.AchievementAPI;
import org.gem.indo.dooit.api.responses.AchievementResponse;

import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/25.
 */

public class AchievementManager extends DooitManager {

    private final AchievementAPI achievementAPI;

    public AchievementManager(Application application) {
        super(application);
        achievementAPI = retrofit.create(AchievementAPI.class);
    }

    public Observable<AchievementResponse> retrieveAchievement(long userId, DooitErrorHandler errorHandler) {
        return useNetwork(achievementAPI.getAchievement(userId), errorHandler);
    }
}
