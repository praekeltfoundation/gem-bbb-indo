package com.rr.rgem.gem.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Wimpie Victor on 2016/10/20.
 */
public class ImageDownloader {

    private static final String TAG = "ImageDownloader";

    OkHttpClient client;
    Handler handler;

    public ImageDownloader(OkHttpClient client) {
        this.client = client;
        handler = new Handler(Looper.getMainLooper());
    }

    public void retrieveImage(final String url, final ImageCallback callback) {
        retrieveImage(url, false, callback);
    }

    public void retrieveImage(final String url, boolean refresh, final ImageCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, String.format("Downloaded [%d] %s status", response.code(), url));
                if (response.isSuccessful()) {
                    InputStream stream = response.body().byteStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoad(bitmap);
                        }
                    });
                } else {
                    Log.d(TAG, "Image download failed");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Image download failed", e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure();
                    }
                });
            }
        });
    }

}
