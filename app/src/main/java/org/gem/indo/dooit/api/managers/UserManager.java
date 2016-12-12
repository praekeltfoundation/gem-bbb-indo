package org.gem.indo.dooit.api.managers;

import android.app.Application;
import android.support.annotation.NonNull;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.UserAPI;
import org.gem.indo.dooit.api.requests.ChangePassword;
import org.gem.indo.dooit.api.requests.ChangeSecurityQuestion;
import org.gem.indo.dooit.api.requests.ChangeUser;
import org.gem.indo.dooit.api.requests.ResetPassword;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.models.User;

import javax.inject.Inject;

import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by herman on 2016/11/05.
 */

public class UserManager extends DooitManager {

    private final UserAPI userAPI;

    @Inject
    public UserManager(Application application) {
        super(application);
        userAPI = retrofit.create(UserAPI.class);
    }

    public Observable<User> retrieveUser(int userId, DooitErrorHandler errorHandler) {
        return useNetwork(userAPI.getUser(userId), errorHandler);
    }

    public Observable<EmptyResponse> updateUser(long userId, String name, DooitErrorHandler errorHandler) {
        return useNetwork(userAPI.renameUser(userId,new ChangeUser(name)), errorHandler);
    }

    public Observable<EmptyResponse> changePassword(long userId, String oldPassword,String newPassword,  DooitErrorHandler errorHandler) {
        return useNetwork(userAPI.changePassword(userId,new ChangePassword(oldPassword,newPassword)), errorHandler);
    }

    public Observable<EmptyResponse> alterSecurityQuestion(String question, String answer, DooitErrorHandler errorHandler) {
        return useNetwork(userAPI.alterSecurityQuestion(new ChangeSecurityQuestion(question, answer)), errorHandler);
    }

    public Observable<String> getSecurityQuestion(String username, DooitErrorHandler errorHandler) {
        return useNetwork(userAPI.getSecurityQuestion(username), errorHandler);
    }

    public Observable<EmptyResponse> submitSecurityQuestionResponse(String username, String answer, String password, DooitErrorHandler errorHandler) {
        return useNetwork(userAPI.submitSecurityQuestionResponse(new ResetPassword(username, answer, password)), errorHandler);
    }
}
