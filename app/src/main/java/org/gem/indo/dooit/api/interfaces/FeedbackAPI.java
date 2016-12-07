package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.models.UserFeedback;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Rudolph Jacobs on 2016-12-06.
 */

public interface FeedbackAPI {
    @POST("/api/feedback/")
    Observable<UserFeedback> sendFeedback(@Body UserFeedback feedback);
}
