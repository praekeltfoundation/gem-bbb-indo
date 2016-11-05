package com.nike.dooit.api.managers;

import android.app.Application;

import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.interfaces.FileUploadAPI;
import com.nike.dooit.api.managers.DooitManager;
import com.nike.dooit.api.responses.EmptyResponse;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by Bernhard Müller on 2016/11/05.
 */
public class FileUploadManager extends DooitManager {
    private final FileUploadAPI fileUploadAPI;

    @Inject
    public FileUploadManager(Application application) {
        super(application);
        fileUploadAPI = retrofit.create(FileUploadAPI.class);
    }

    @Override
    protected Request.Builder addTokenToRequest(Request.Builder requestBuilder) {
        return requestBuilder;
    }

    public Observable<EmptyResponse> upload(String userId, String mediaType, File file, DooitErrorHandler errorHandler) {
        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        String filename = file.getName();
        return useNetwork(fileUploadAPI.upload(userId, RequestBody.create(mediaTypeHeader, file), "attachment;filename=\"" + filename + "\""), errorHandler);
    }
}
