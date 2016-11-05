package com.nike.dooit.dagger;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.api.managers.DooitManager;
import com.nike.dooit.util.Persisted;
import com.nike.dooit.views.profile.ProfileActivity;
import com.nike.dooit.views.settings.SettingsActivity;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.RootActivity;
import com.nike.dooit.views.onboarding.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by herman on 2016/11/05.
 */
@Singleton
@Component(modules = DooitModule.class)
public interface DooitComponent {

    void inject(DooitManager o);

    void inject(RootActivity o);

    void inject(DooitApplication o);

    void inject(DooitActivity o);

    void inject(ProfileActivity o);

    void inject(LoginActivity o);

    void inject(SettingsActivity o);

    void inject(Persisted o);
}
