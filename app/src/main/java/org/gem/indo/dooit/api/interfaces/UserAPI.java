package org.gem.indo.dooit.api.interfaces;

import org.gem.indo.dooit.api.requests.ChangePassword;
import org.gem.indo.dooit.api.requests.ChangeSecurityQuestion;
import org.gem.indo.dooit.api.requests.ChangeUser;
import org.gem.indo.dooit.api.requests.ResetPassword;
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

    @POST("/api/users/change_security_question/")
    Observable<EmptyResponse> alterSecurityQuestion(@Body ChangeSecurityQuestion changeQuestion);

    @GET("/api/security_question/")
    Observable<String> getSecurityQuestion(
        @Query("username") String username
    );

    @POST("/api/security_question/")
    Observable<EmptyResponse> submitSecurityQuestionResponse(@Body ResetPassword resetPassword);

}
