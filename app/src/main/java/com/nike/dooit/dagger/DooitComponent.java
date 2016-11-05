package com.nike.dooit.dagger;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.RootActivity;
import com.nike.dooit.api.managers.DooitManager;
import com.nike.dooit.views.profile.ProfileActivity;
import com.nike.dooit.views.settings.SettingsActivity;
import com.nike.dooit.views.DooitActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by herman on 2016/11/05.
 */
@Singleton
@Component(modules = DooitModule.class)
public interface DooitComponent {

    void inject(DooitManager dooitManager);

    void inject(RootActivity rootActivity);

    void inject(DooitApplication dooitApplication);

    void inject(ProfileActivity profileActivity);

    void inject(DooitActivity dooitActivity);

    void inject(SettingsActivity settingsActivity);
}
