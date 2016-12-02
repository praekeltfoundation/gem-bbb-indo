package org.gem.indo.dooit.api.managers.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chris on 2016-12-02.
 */

public class DooitCacheControl {

    public interface NetworkMonitor {
        boolean isCacheDisabled(Request request);
        boolean isOnline();
    }

    public interface MaxAgeControl {
        /**
         * @return max-age in seconds
         */
        long getMaxAge();
    }

    public static DooitCacheControl on(OkHttpClient.Builder okBuilder) {
        DooitCacheControl builder = new DooitCacheControl(okBuilder);
        return builder;
    }


    private DooitCacheControl.NetworkMonitor networkMonitor;
    private long maxAgeValue;
    private TimeUnit maxAgeUnit;
    private OkHttpClient.Builder okBuilder;
    private DooitCacheControl.MaxAgeControl maxAgeControl;

    private DooitCacheControl(OkHttpClient.Builder okBuilder) {
        this.okBuilder = okBuilder;
    }

    public DooitCacheControl overrideServerCachePolicy(Long maxAgeSeconds) {
        if (maxAgeSeconds == null) {
            return this.overrideServerCachePolicy(0, null);
        } else {
            return this.overrideServerCachePolicy(maxAgeSeconds, TimeUnit.SECONDS);
        }
    }

    public DooitCacheControl overrideServerCachePolicy(long timeValue, TimeUnit unit) {
        this.maxAgeControl = null;
        this.maxAgeValue = timeValue;
        this.maxAgeUnit = unit;
        return this;
    }

    public DooitCacheControl overrideServerCachePolicy(DooitCacheControl.MaxAgeControl maxAgeControl) {
        this.maxAgeUnit = null;
        this.maxAgeControl = maxAgeControl;
        return this;
    }

    public DooitCacheControl forceCacheWhenOffline(DooitCacheControl.NetworkMonitor networkMonitor) {
        this.networkMonitor = networkMonitor;
        return this;
    }

    public OkHttpClient.Builder apply() {
        if (networkMonitor == null && maxAgeUnit == null && maxAgeControl == null) {
            return okBuilder;
        }

        if (maxAgeUnit != null) {
            maxAgeControl = new DooitCacheControl.StaticMaxAgeControl(maxAgeValue, maxAgeUnit);
        }

        DooitCacheControl.ResponseHandler responseHandler;
        if (maxAgeControl != null) {
            responseHandler = new DooitCacheControl.CachePolicyResponseHandler(maxAgeControl);
        } else {
            responseHandler = new DooitCacheControl.ResponseHandler();
        }

        DooitCacheControl.RequestHandler requestHandler;
        if (networkMonitor != null) {
            requestHandler = new DooitCacheControl.NetworkMonitorRequestHandler(networkMonitor);
        } else {
            requestHandler = new DooitCacheControl.RequestHandler();
        }

        Interceptor cacheControlInterceptor = getCacheControlInterceptor(
                requestHandler, responseHandler);

        okBuilder.addNetworkInterceptor(cacheControlInterceptor);

        if (networkMonitor != null) {
            okBuilder.addInterceptor(cacheControlInterceptor);
        }

        return okBuilder;
    }

    private static class StaticMaxAgeControl implements DooitCacheControl.MaxAgeControl {
        private TimeUnit maxAgeUnit;
        private long maxAgeValue;

        private StaticMaxAgeControl(long maxAgeValue, TimeUnit maxAgeUnit) {
            this.maxAgeUnit = maxAgeUnit;
            this.maxAgeValue = maxAgeValue;
        }

        @Override
        public long getMaxAge() {
            return maxAgeUnit.toSeconds(maxAgeValue);
        }
    }

    private static class CachePolicyResponseHandler extends DooitCacheControl.ResponseHandler {
        private DooitCacheControl.MaxAgeControl maxAgeControl;

        private CachePolicyResponseHandler(DooitCacheControl.MaxAgeControl maxAgeControl) {
            this.maxAgeControl = maxAgeControl;
        }

        @Override
        public Response newResponse(Response response) {
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "max-age=" + maxAgeControl.getMaxAge())
                    .build();
        }
    }

    private static class NetworkMonitorRequestHandler extends DooitCacheControl.RequestHandler {
        private DooitCacheControl.NetworkMonitor networkMonitor;

        private NetworkMonitorRequestHandler(DooitCacheControl.NetworkMonitor networkMonitor) {
            this.networkMonitor = networkMonitor;
        }

        @Override
        public Request newRequest(Request request) {
            Request.Builder newBuilder = request.newBuilder();
            if(networkMonitor.isCacheDisabled(request)){
                newBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                return newBuilder.build();
            }
            if (!networkMonitor.isOnline()) {
                // To be used with Application Interceptor to use Expired cache
                newBuilder.cacheControl(CacheControl.FORCE_CACHE);
            }
            return newBuilder.build();
        }
    }

    private static Interceptor getCacheControlInterceptor(final DooitCacheControl.RequestHandler requestHandler,
                                                          final DooitCacheControl.ResponseHandler responseHandler) {
        return new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request request = requestHandler.newRequest(originalRequest);

                Response originalResponse = chain.proceed(request);

                return responseHandler.newResponse(originalResponse);
            }
        };
    }

    private static class ResponseHandler {
        public Response newResponse(Response response) {
            return response;
        }
    }

    private static class RequestHandler {
        public Request newRequest(Request request) {
            return request;
        }
    }
}
