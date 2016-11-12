package com.nike.dooit.dagger;

import android.app.Application;
import android.content.Context;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.managers.ChallengeManager;
import com.nike.dooit.api.managers.FileUploadManager;
import com.nike.dooit.api.managers.GoalManager;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.api.managers.UserManager;
import com.nike.dooit.helpers.bot.BotFeed;
import com.nike.dooit.helpers.permissions.PermissionsHelper;
import com.nike.dooit.helpers.DooitSharedPreferences;
import com.nike.dooit.helpers.Persisted;

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
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    AuthenticationManager provideAuthenticationManager() {
        return new AuthenticationManager(application);
    }

    @Provides
    @Singleton
    ChallengeManager provideChallengeManager() {
        return new ChallengeManager(application);
    }

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
    GoalManager provideGoalManager() {
        return new GoalManager(application);
    }

    @Provides
    @Singleton
    Persisted providePersisted() {
        return new Persisted(application);
    }

    @Provides
    @Singleton
    PermissionsHelper providePermissionsHelper() {
        return new PermissionsHelper();
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

    @Provides
    @Singleton
    BotFeed provideGoalBotFeed() {
        return new BotFeed(application);
    }
}
