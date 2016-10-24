package com.rr.rgem.gem.service;

import android.util.Log;

import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.image.ImageDownloader;
import com.rr.rgem.gem.service.model.AuthLogin;
import com.rr.rgem.gem.service.model.AuthToken;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wimpie Victor on 2016/10/14.
 */
public class WebServiceFactory {

    // Builders are stored to leverage shared cache and thread pools between http clients.
    private final String baseUrl;
    private OkHttpClient.Builder clientBuilder;
    private Retrofit.Builder retrofitBuilder;
    private AuthStore store;

    public WebServiceFactory(String baseUrl, AuthStore store) {
        this.baseUrl = baseUrl;
        this.store = store;
        clientBuilder = createHttpClientBuilder(true);
        retrofitBuilder = createRetrofitBuilder(baseUrl);
    }

    /**
     * @param shouldAuthenticate False prevents recursive loop with Authenticator and Interceptor.
     */
    OkHttpClient.Builder createHttpClientBuilder(boolean shouldAuthenticate) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (shouldAuthenticate) {
            AuthService authService = createAuthService();
            builder.authenticator(new TokenAuthenticator(authService));
            builder.addInterceptor(new AuthenticationInterceptor(store, authService));
        } else {
            builder.authenticator(Authenticator.NONE);
        }
        return builder;
    }

    Retrofit.Builder createRetrofitBuilder(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callbackExecutor(new MainThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create());
    }

    /**
     * Because the Authenticator and Interceptor make HTTP requests to the token endpoint, they
     * require their own HTTP client where they themselves are not included.
     */
    public AuthService createAuthService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(new MainThreadExecutor())
                .client(createHttpClientBuilder(false).build())
                .build()
                .create(AuthService.class);
    }

    public <T> T createService(Class<T> serviceClass) {
        return retrofitBuilder
                .client(clientBuilder.build())
                .build()
                .create(serviceClass);
    }

    public Retrofit createRetrofit() {
        return retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ImageDownloader createImageDownloader() {
        return new ImageDownloader(baseUrl, getClient());
    }

    public OkHttpClient getClient() {
        return clientBuilder.build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String joinUrl(String relativeUrl) throws MalformedURLException {
        return new URL(new URL(baseUrl), relativeUrl).toString();
    }

    private class TokenAuthenticator implements Authenticator {

        private AuthService authService;

        public TokenAuthenticator(AuthService authService) {
            this.authService = authService;
        }

        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            Log.d("Authenticator", "Authenticating");
            return null;
        }
    }

    private class AuthenticationInterceptor implements Interceptor {

        private static final String TAG = "AuthInterceptor";
        private AuthStore store;
        private AuthService authService;

        public AuthenticationInterceptor(AuthStore store, AuthService authService) {
            this.store = store;
            this.authService = authService;

            if (store == null) {
                throw new NullPointerException("Auth Store is null");
            }
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.d(TAG, "In AuthenticationInterceptor " + request.url());

            AuthToken token = store.loadToken();
            if (token.hasToken()) {
                Request authRequest = request.newBuilder()
                        .header("Authorization", token.getTokenHeader())
                        .build();
                return chain.proceed(authRequest);
            } else {
                return chain.proceed(request);
            }
        }
    }
}
