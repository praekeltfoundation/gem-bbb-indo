package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.api.responses.PictureParticipationResponse;

import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Bernhard Müller on 2016/11/05.
 */
public interface FileUploadAPI {

    @POST("/api/profile-image/{id}/")
    Observable<Response<EmptyResponse>> uploadProfileImage(@Path("id") long id, @Body RequestBody requestBody, @Header("Content-Disposition") String contentDisposition);

    @POST("/api/participant-image/{id}/")
    Observable<PictureParticipationResponse> uploadParticipantPicture(@Path("id") long id, @Body RequestBody requestBody, @Header("Content-Disposition") String contentDisposition);

    @POST("/api/goal-image/{id}/")
    Observable<EmptyResponse> uploadGoalImage(@Path("id") long goalId, @Body RequestBody requestBody, @Header("Content-Disposition") String contentDisposition);
}
