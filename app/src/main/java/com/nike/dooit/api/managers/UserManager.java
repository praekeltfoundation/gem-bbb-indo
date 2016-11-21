package com.nike.dooit.api.managers;

import android.app.Application;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.interfaces.UserAPI;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.models.User;
import com.nike.dooit.api.requests.ChangeUser;
import javax.inject.Inject;

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
}
