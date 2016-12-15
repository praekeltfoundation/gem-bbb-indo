package org.gem.indo.dooit.api.managers;

import android.app.Application;

import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.interfaces.FileUploadAPI;
import org.gem.indo.dooit.api.responses.EmptyResponse;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
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

    protected static String makeContentDisposition(String filename) {
        return String.format("attachment;filename=\"%s\"", filename);
    }

    public Observable<Response<EmptyResponse>> uploadProfileImage(long userId, String mediaType, File file, DooitErrorHandler errorHandler) {
        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        String filename = file.getName();
        return useNetwork(fileUploadAPI.uploadProfileImage(userId, RequestBody.create(mediaTypeHeader, file), "attachment;filename=\"" + filename + "\""), errorHandler);
    }

    public Observable<EmptyResponse> uploadParticipantPicture(long participantId, String mediaType, File file, DooitErrorHandler errorHandler) {
        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        String filename = file.getName();
        return useNetwork(fileUploadAPI.uploadParticipantPicture(participantId, RequestBody.create(mediaTypeHeader, file), "attachment;filename=\"" + filename + "\""), errorHandler);
    }

    public Observable<EmptyResponse> uploadGoalImage(long goalId, String mediaType, File file, DooitErrorHandler errorHandler) {
        MediaType mediaTypeHeader = MediaType.parse(mediaType);
        String filename = file.getName();
        String contentDisposition = makeContentDisposition(filename);
        return useNetwork(fileUploadAPI.uploadGoalImage(goalId, RequestBody.create(mediaTypeHeader, file), contentDisposition), errorHandler);
    }
}
