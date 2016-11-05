package com.nike.dooit.dagger;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.RootActivity;
import com.nike.dooit.api.managers.DooitManager;

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
}
