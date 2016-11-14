package com.nike.dooit.api.interfaces;

import com.nike.dooit.api.responses.EmptyResponse;

import okhttp3.RequestBody;
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
    Observable<EmptyResponse> upload(@Path("id") long id, @Body RequestBody requestBody, @Header("Content-Disposition") String contentDisposition);
}
