package com.nike.dooit;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nike.dooit.dagger.DaggerDooitComponent;
import com.nike.dooit.dagger.DooitComponent;
import com.nike.dooit.dagger.DooitModule;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by herman on 2016/11/05.
 */

public class DooitApplication extends Application {

    public DooitComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Constants.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        Fresco.initialize(this);
        component = DaggerDooitComponent.builder()
                .dooitModule(new DooitModule(this))
                .build();
        component.inject(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LondrinaSolid-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
