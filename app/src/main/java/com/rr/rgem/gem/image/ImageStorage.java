package com.rr.rgem.gem.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Wimpie Victor on 2016/10/20.
 */
public class ImageStorage {

    private static final String TAG = "ImageStorage";

    Context context;
    String directory;

    public ImageStorage(Context context, String directory) {
        this.context = context;
        this.directory = directory;
    }

    public Bitmap loadImage(String filename) {
        File dir = context.getDir(directory, Context.MODE_PRIVATE);
        File file = new File(dir, fileId(filename));
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return BitmapFactory.decodeStream(stream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Attempt to load image, not found", e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException while attempting to close stream", e);
            }
        }
        return null;
    }

    public void saveImage(String filename, Bitmap bitmap) {
        File dir = context.getDir(directory, Context.MODE_PRIVATE);
        File file = new File(dir, fileId(filename));
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Attempt to save image, not found", e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException while attempting to close stream", e);
            }
        }
    }


    public void clearDirectory() {
        File dir = context.getDir(directory, Context.MODE_PRIVATE);
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    public void deleteFile(String filename) {
        File file = new File(filename);
        file.delete();
    }

    public static Bitmap scaleDownBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxWidth > 0 && maxHeight > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;

            if (ratioMax > 1) {
                // Landscape
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                // Portrait
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
        } else {
            return bitmap;
        }
    }

    protected static String fileId(String filepath) {
        return filepath.replace('/', '-');
    }
}
