package org.gem.indo.dooit;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import org.gem.indo.dooit.dagger.DaggerDooitComponent;
import org.gem.indo.dooit.dagger.DooitComponent;
import org.gem.indo.dooit.dagger.DooitModule;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.Tls12SocketFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.CipherSuite;
import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by herman on 2016/11/05.
 */

public class DooitApplication extends MultiDexApplication {

    public DooitComponent component;

    @Inject
    Persisted persisted;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        // Dagger
        component = DaggerDooitComponent.builder()
                .dooitModule(new DooitModule(this))
                .build();
        component.inject(this);

        // Realm
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build());

        // OkHttp
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
                Response response = chain.proceed(request);
                if (response != null) {
                    Handshake handshake = response.handshake();
                    if (handshake != null) {
                        final CipherSuite cipherSuite = handshake.cipherSuite();
                        final TlsVersion tlsVersion = handshake.tlsVersion();
                        Log.v("DooitApplication", "TLS: " + tlsVersion + ", CipherSuite: " + cipherSuite);
                    }
                }
                return chain.proceed(request);
            }
        });

        Tls12SocketFactory.forceTLS(httpClient);

        httpClient.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        // Fresco
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, httpClient.build())
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);

        if (Constants.DEBUG)
            FLog.setMinimumLoggingLevel(FLog.DEBUG);

        // Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }


}
