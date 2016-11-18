package com.nike.dooit;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.nike.dooit.dagger.DaggerDooitComponent;
import com.nike.dooit.dagger.DooitComponent;
import com.nike.dooit.dagger.DooitModule;
import com.nike.dooit.helpers.Persisted;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by herman on 2016/11/05.
 */

public class DooitApplication extends Application {

    public DooitComponent component;

    @Inject
    Persisted persisted;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Constants.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        component = DaggerDooitComponent.builder()
                .dooitModule(new DooitModule(this))
                .build();
        component.inject(this);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(original.url())
                        .addHeader("Accept", "application/json")
                        .method(original.method(), original.body());

                if (persisted.hasToken())
                    requestBuilder.addHeader("Authorization", "Token " + persisted.getToken());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, httpClient.build())
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);

        if (Constants.DEBUG)
            FLog.setMinimumLoggingLevel(FLog.DEBUG);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LondrinaSolid-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
