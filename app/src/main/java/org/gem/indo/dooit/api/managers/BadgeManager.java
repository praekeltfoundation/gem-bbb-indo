package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.BadgeAPI;
import org.gem.indo.dooit.api.responses.BadgeImageUrlsResponse;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Reinhardt on 2017/02/13.
 */

public class BadgeManager extends DooitManager {
    private final BadgeAPI badgeAPI;

    @Inject
    public BadgeManager(Application application) {
        super(application);
        badgeAPI = retrofit.create(BadgeAPI.class);
    }

    public Observable<BadgeImageUrlsResponse> getBadgeUrls(DooitErrorHandler errorHandler){
        return useNetwork(badgeAPI.getBadgeUrls(), errorHandler);
    }
}
