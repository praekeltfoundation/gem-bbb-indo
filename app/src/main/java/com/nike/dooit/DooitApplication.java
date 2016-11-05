package com.nike.dooit;

import android.app.Application;

import com.nike.dooit.dagger.DaggerDooitComponent;
import com.nike.dooit.dagger.DooitComponent;
import com.nike.dooit.dagger.DooitModule;

/**
 * Created by herman on 2016/11/05.
 */

public class DooitApplication extends Application {

    public DooitComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerDooitComponent.builder()
                .dooitModule(new DooitModule(this))
                .build();
        component.inject(this);
    }
}
