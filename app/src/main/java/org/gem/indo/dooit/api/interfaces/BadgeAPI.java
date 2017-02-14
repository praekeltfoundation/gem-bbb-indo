package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.BadgeImageUrlsResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Reinhardt on 2017/02/13.
 */

public interface BadgeAPI  {
    @GET("/api/badge-urls/")
    Observable<BadgeImageUrlsResponse> getBadgeUrls();
}
