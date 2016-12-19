package org.gem.indo.dooit.helpers;

import android.graphics.Bitmap;

/**
 * Created by Wimpie Victor on 2016/12/19.
 */

public class ImageScaler {

    public static Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > height) {
            // Landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // Portrait
            float ratio = (float) height / maxHeight;
            width = (int) (width / ratio);
            height = maxHeight;
        } else {
            // Square
            width = maxWidth;
            height = maxHeight;
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
}
