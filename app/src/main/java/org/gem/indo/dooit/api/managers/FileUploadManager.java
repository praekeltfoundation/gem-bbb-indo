package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.FileUploadAPI;
import org.gem.indo.dooit.api.responses.EmptyResponse;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by Bernhard Müller on 2016/11/05.
 */
public class FileUploadManager extends DooitManager {
    private final FileUploadAPI fileUploadAPI;

    @Inject
    public FileUploadManager(Application application) {
        super(application,false);
        fileUploadAPI = retrofit.create(FileUploadAPI.class);
    }

    public Observable<EmptyResponse> upload(long userId, String mediaType, File file, DooitErrorHandler errorHandler) {
        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        String filename = file.getName();
        return useNetwork(fileUploadAPI.upload(userId, RequestBody.create(mediaTypeHeader, file), "attachment;filename=\"" + filename + "\""), errorHandler);
    }

    public Observable<EmptyResponse> uploadParticipantPicture(long participantId, String mediaType, File file, DooitErrorHandler errorHandler) {
        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        String filename = file.getName();
        return useNetwork(fileUploadAPI.uploadParticipantPicture(participantId, RequestBody.create(mediaTypeHeader, file), "attachment;filename=\"" + filename + "\""), errorHandler);
    }
}
