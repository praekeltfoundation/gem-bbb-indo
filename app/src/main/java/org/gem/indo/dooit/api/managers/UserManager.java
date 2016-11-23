package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.UserAPI;
import org.gem.indo.dooit.models.User;

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
}
