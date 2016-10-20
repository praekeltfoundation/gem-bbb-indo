package com.rr.rgem.gem.image;

import android.graphics.Bitmap;

/**
 * Created by Wimpie Victor on 2016/10/20.
 */
public interface ImageCallback {
    void onLoad(Bitmap image);
    void onFailure();
}
