package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.requests.ChangePassword;
import org.gem.indo.dooit.api.requests.ChangeUser;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.models.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public interface UserAPI {

    @GET("/api/users/{id}/")
    Observable<User> getUser(
        @Path("id") int id
    );

    @PATCH("/api/users/{id}/")
    Observable<EmptyResponse> renameUser(
        @Path("id") long id,
        @Body ChangeUser name
    );

    @POST("/api/users/{id}/password/")
    Observable<EmptyResponse> changePassword(
        @Path("id") long id,
        @Body ChangePassword pw
    );

    @Multipart
    @POST("/api/users/change_security_question/")
    Observable<EmptyResponse> alterSecurityQuestion(
        @Part("new_question") String question,
        @Part("new_answer") String answer
    );

    @GET("/api/security_question/")
    Observable<String> getSecurityQuestion(
        @Query("username") String username
    );

    @Multipart
    @POST("/api/security_question/")
    Observable<EmptyResponse> submitSecurityQuestionResponse(
        @Query("username") String username,
        @Part("answer") String answer,
        @Part("new_password") String password
    );

}
