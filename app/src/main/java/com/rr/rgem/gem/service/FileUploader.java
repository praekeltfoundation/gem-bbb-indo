package com.rr.rgem.gem.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Wimpie Victor on 2016/10/21.
 */
public class FileUploader {

    private static final String TAG = "FileUploader";

    OkHttpClient client;
    Handler handler;

    public FileUploader(OkHttpClient client) {
        this.client = client;
        handler = new Handler(Looper.getMainLooper());
    }

    public void send(final String url, String mediaType, File file, final UploadCallback callback) {
        Log.d(TAG, String.format("Uploading [%s] %s", mediaType, url));

        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaTypeHeader, file))
                .header("Content-Disposition", "attachment;filename=\"profile.jpg\"")
                .build();

        Log.d(TAG, request.headers().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Response " + response.code());
                Log.d(TAG, response.body().string());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failure", e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure();
                    }
                });
            }
        });
    }

    public interface UploadCallback {
        void onComplete();
        void onFailure();
    }
}
