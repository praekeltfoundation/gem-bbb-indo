package org.gem.indo.dooit.helpers.images;

import android.net.Uri;

/**
 * Created by Reinhardt on 2017/02/27.
 */

public interface ImageSelectedListener {
    public void handleSelectedImage(String mediaType, Uri imageUri, String imagePath);
}
