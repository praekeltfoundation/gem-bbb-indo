package org.gem.indo.dooit.dagger;

import android.app.Application;
import android.content.Context;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.managers.AchievementManager;
import org.gem.indo.dooit.api.managers.AuthenticationManager;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.api.managers.FeedbackManager;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.helpers.global.GlobalVariables;
import org.gem.indo.dooit.helpers.InvalidTokenHandler.InvalidTokenHandler;
import org.gem.indo.dooit.helpers.InvalidTokenHandler.OpenLoginHandler;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultHelper;
import org.gem.indo.dooit.helpers.bot.BotFeed;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;

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
    ActivityForResultHelper provideActivityForResultHelper() {
        return new ActivityForResultHelper();
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
    AchievementManager provideAchievementManager() {
        return new AchievementManager(application);
    }

    @Provides
    @Singleton
    ChallengeManager provideChallengeManager() {
        return new ChallengeManager(application);
    }

    @Provides
    @Singleton
    DooitSharedPreferences provideDooitSharedPreferences() {
        return new DooitSharedPreferences(application);
    }

    @Provides
    @Singleton
    FeedbackManager provideFeedbackManager() {
        return new FeedbackManager(application);
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

    @Provides
    @Singleton
    GoalManager provideGoalManager() {
        return new GoalManager(application);
    }

    @Provides
    @Singleton
    PermissionsHelper providePermissionsHelper() {
        return new PermissionsHelper();
    }

    @Provides
    @Singleton
    Persisted providePersisted() {
        return new Persisted(application);
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
    InvalidTokenHandler provideInvalidTokenHandler() {
        return new OpenLoginHandler();
    }

    @Provides
    @Singleton
    GlobalVariables provideGlobalClass() {
        return new GlobalVariables();
    }
}
