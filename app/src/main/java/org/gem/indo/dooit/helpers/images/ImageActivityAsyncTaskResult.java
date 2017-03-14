package org.gem.indo.dooit.helpers.images;

import android.net.Uri;

/**
 * Created by Reinhardt on 2017/03/03.
 */

public class ImageActivityAsyncTaskResult {

    Uri imageUri;
    String imagePath;

    public ImageActivityAsyncTaskResult(Uri imageUri, String imagePath) {
        this.imageUri = imageUri;
        this.imagePath = imagePath;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getImagePath() {
        return imagePath;
    }
}
