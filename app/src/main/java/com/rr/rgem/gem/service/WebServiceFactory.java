package com.rr.rgem.gem.service;

import android.util.Log;

import com.rr.rgem.gem.service.model.AuthLogin;
import com.rr.rgem.gem.service.model.AuthToken;

import java.io.IOException;

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
    private AuthToken token;

    public WebServiceFactory(String baseUrl) {
        this.baseUrl = baseUrl;
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
            builder.addInterceptor(new AuthenticationInterceptor(this, authService));
        } else {
            builder.authenticator(Authenticator.NONE);
        }
        return builder;
    }

    Retrofit.Builder createRetrofitBuilder(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());
    }

    AuthService createAuthService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
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

    public OkHttpClient getClient() {
        return clientBuilder.build();
    }

    public boolean hasToken() {
        return token != null;
    }

    public AuthToken getToken() {
        return token;
    }

    void setToken(AuthToken token) {
        this.token = token;
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

        private WebServiceFactory factory;
        private AuthService authService;

        public AuthenticationInterceptor(WebServiceFactory factory, AuthService authService) {
            this.factory = factory;
            this.authService = authService;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response originalResponse = chain.proceed(request);
            Log.d("Interceptor", "In AuthenticationInterceptor " + request.url());

            if (originalResponse.code() == 403) {
                Log.d("Interceptor", "");
                AuthToken token;
                if (factory.hasToken()) {
                    Log.d("Interceptor", "Token exists");
                    token = factory.getToken();
                } else {
                    // TODO: Get login credentials
                    Log.d("Interceptor", "Retrieving token from service");
                    token = authService.createToken(new AuthLogin("wimpie", "2")).execute().body();
                }
                Log.d("Interceptor", String.format("Interceptor got token: %s", token));
                Request authRequest = request.newBuilder()
                        .header("Authorization", token.getTokenHeader())
                        .build();
                Response newResponse = chain.proceed(authRequest);
                Log.d("Interceptor", String.format("New status after proceed %d", newResponse.code()));
                return newResponse;
            } else {
                return originalResponse;
            }
        }
    }
}
