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

    public DooitManager(Application application) {
        ((DooitApplication) application).component.inject(this);
        context = application.getApplicationContext();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

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

        OkHttpClient client = httpClient.build();

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