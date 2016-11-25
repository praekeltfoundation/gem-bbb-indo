package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.AchievementResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Wimpie Victor on 2016/11/25.
 */

public interface AchievementAPI {
    @GET("/api/achievements/{user_id}/")
    Observable<AchievementResponse> getAchievement(@Path("user_id") long userId);
}
