package org.gem.indo.dooit.api.managers;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.serializers.ChallengeSerializer;
import org.gem.indo.dooit.api.serializers.DateTimeSerializer;
import org.gem.indo.dooit.api.serializers.LocalDateSerializer;
import org.gem.indo.dooit.helpers.DooitSharedPreferences;
import org.gem.indo.dooit.helpers.Persisted;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by herman on 2016/11/05.
 */

public class DooitManager {

    protected Retrofit retrofit;
    @Inject
    DooitSharedPreferences sharedPreferences;
    @Inject
    Persisted persisted;
    private Context context;
    private static final Interceptor RewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if(false){
                /// other example code to use cache as fallback mechanism for network issue
                boolean hasNetwork = false;
                Request request = chain.request();
                if (hasNetwork) {
                    request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                } else {
                    request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                }
                return chain.proceed(request);
            }
            Request originalRequest = chain.request();
            //Request.Builder request = originalRequest.newBuilder();
            //if (originalRequest.header("fresh") != null) {
            //    request.cacheControl(CacheControl.FORCE_NETWORK);
            //}
            Response originalResponse = chain.proceed(originalRequest);

            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", String.format("max-age=%d, only-if-cached, max-stale=%d", 30*60, 0))
                    .build();
        }
    };
    public DooitManager(final Application application) {
        ((DooitApplication) application).component.inject(this);
        context = application.getApplicationContext();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        final Cache cache = new Cache(application.getApplicationContext().getCacheDir(), 1024 * 1024 * 30);

        httpClient.addInterceptor( new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                int hc = cache.hitCount();
                Request originalRequest = chain.request();
                Response originalResponse = chain.proceed(originalRequest);

                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", String.format("max-age=%d, only-if-cached, max-stale=%d", 30*60, 0))
                        .build();
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("okhttp", message, null);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(original.url())
                        .addHeader("Accept", "application/json")
                        .method(original.method(), original.body());

                requestBuilder = addTokenToRequest(requestBuilder);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient.cache(cache).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .registerTypeAdapter(DateTime.class, new DateTimeSerializer())
                                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                                .registerTypeAdapterFactory(ChallengeSerializer.getChallengeAdapterFactory())
                                .create())
                )
                .build();
    }

    protected Request.Builder addTokenToRequest(Request.Builder requestBuilder) {
        if (persisted.hasToken())
            requestBuilder.addHeader("Authorization", "Token " + persisted.getToken());
        return requestBuilder;
    }

    private <T> Observable<T> addErrorHandling(Observable<T> observable,
                                               DooitErrorHandler errorHandler) {
        errorHandler.attachContext(context);
        return observable.retry(new Func2<Integer, Throwable, Boolean>() {
            @Override
            public Boolean call(Integer count, Throwable throwable) {
                return count <= 3 && !(throwable instanceof HttpException);
            }
        })
                .doOnError(errorHandler)
                .onErrorResumeNext(Observable.<T>empty())
                .onExceptionResumeNext(Observable.<T>empty());
    }

    protected <T> Observable<T> useNetwork(Observable<T> networkObservable,
                                           DooitErrorHandler errorHandler) {
        return addErrorHandling(networkObservable, errorHandler)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }


    protected <T> Observable<T> useNetwork(Observable<T> networkObservable, Scheduler observeOn,
                                           DooitErrorHandler errorHandler) {
        return addErrorHandling(networkObservable, errorHandler)
                .subscribeOn(Schedulers.newThread())
                .observeOn(observeOn);
    }

    protected <T> Observable<T> useNetworkDelete(Observable<T> networkObservable,
                                                 DooitErrorHandler errorHandler) {
        return addErrorHandling(networkObservable, errorHandler)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }
}
