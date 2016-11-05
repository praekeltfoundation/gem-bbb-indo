package com.nike.dooit.dagger;

import android.app.Application;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.managers.FileUploadManager;
import com.nike.dooit.api.managers.ChallengeManager;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.api.managers.UserManager;
import com.nike.dooit.util.DooitSharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by herman on 2016/11/05.
 */

@Module
public class DooitModule {

    private final Application application;

    public DooitModule(DooitApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    AuthenticationManager provideAuthenticationManager() { return new AuthenticationManager(application); }

    @Provides
    @Singleton
    ChallengeManager provideChallengeManager() { return new ChallengeManager(application); }

    @Provides
    @Singleton
    TipManager provideTipManager() {
        return new TipManager(application);
    }

    @Provides
    @Singleton
    UserManager provideUserManager() {
        return new UserManager(application);
    }

    @Provides
    @Singleton
    DooitSharedPreferences provideDooitSharedPreferences() {
        return new DooitSharedPreferences(application);
    }

    @Provides
    @Singleton
    FileUploadManager provideFileUploadManager() {
        return new FileUploadManager(application);
    }
}
