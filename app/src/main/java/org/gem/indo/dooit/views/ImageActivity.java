package org.gem.indo.dooit.views;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.images.ImageActivityAsyncTaskResult;
import org.gem.indo.dooit.helpers.images.ImageChooserOptions;
import org.gem.indo.dooit.helpers.images.ImageScaler;
import org.gem.indo.dooit.helpers.images.MediaUriHelper;
import org.gem.indo.dooit.helpers.permissions.PermissionCallback;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wimpie Victor on 2016/12/19.
 */

public abstract class ImageActivity extends DooitActivity {

    private static final String TAG = ImageActivity.class.getName();
    private static final int MAX_IMAGE_WIDTH = 1024;
    private static final int MAX_IMAGE_HEIGHT = 1024;
    final int SCALE_FACTOR_FOR_BITMAP_SIZE = 3;
    private AlertDialog imageChooser;
    private Uri imageUri;
    private String imagePath;

    private void logMemory() {
        try {
            String tag = this.getClass().getName();

            final Runtime runtime = Runtime.getRuntime();
            long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
            long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
            long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            int memoryClass = am.getMemoryClass();

            String msg = String.format(Locale.US,
                    "Memory Class: %d, Max Memory: %dMB, Used Heap: %dMB, Available Heap: %dMB",
                    memoryClass, maxHeapSizeInMB, usedMemInMB, availHeapSizeInMB);

            CrashlyticsHelper.log(tag, "logMemory", msg);
            Log.d(tag, msg);
        } catch (Throwable ex) {
            CrashlyticsHelper.logException(ex);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        CrashlyticsHelper.log(TAG, "rotateImage", "Rotating: " + Float.toString(angle));

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            Bitmap out = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
            if (!source.isRecycled() && !out.sameAs(source)) {
                source.recycle();
            }
            return out;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, String.format("Rotation failed (dim=%dx%d).", source.getWidth(), source.getHeight()), e);
            CrashlyticsHelper.logException(e);
            return source;
        }
    }

    private void resetImageState() {
        imageUri = null;
        imagePath = null;
    }

    protected void showImageChooser() {
        CrashlyticsHelper.log(TAG, "showImageChooser", "Displaying Image Chooser");

        resetImageState();
        final CharSequence[] items = ImageChooserOptions.createMenuItems(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
        builder.setTitle(getString(R.string.label_image_chooser_title));

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (ImageChooserOptions.valueOf(which)) {
                    case CAMERA:
                        CrashlyticsHelper.log(TAG, "showImageChooser", "Starting Camera");
                        startCamera();
                        break;
                    case GALLERY:
                        CrashlyticsHelper.log(TAG, "showImageChooser", "Starting Gallery");
                        startGallery();
                        break;
                    case CANCEL:
                        CrashlyticsHelper.log(TAG, "showImageChooser", "Cancelling Image Chooser");
                        dialog.dismiss();
                        break;
                }
            }
        });

        imageChooser = builder.show();
    }

    public void startCamera() {
        resetImageState();

        permissionsHelper.askForPermission(this, new String[]{PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, PermissionsHelper.D_CAMERA}, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // The file where the full sized photo will be temporarily stored. When this is not
                // done, we would get the thumbnail back instead. See documentation:
                // https://developer.android.com/training/camera/photobasics.html#TaskPath
                File imageFile = null;

                try {
                    imageFile = createImageFile(ImageActivity.this);
                    imagePath = imageFile.getAbsolutePath();
                } catch (IOException e) {
                    Toast.makeText(ImageActivity.this, "Failed to create image file on phone storage", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to create image file on phone storage", e);
                }

                if (imageFile != null) {
                    imageUri = FileProvider.getUriForFile(ImageActivity.this, Constants.FILE_PROVIDER, imageFile);
                    grantCameraPermissions(cameraIntent);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
                    }
                }
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(ImageActivity.this, getString(R.string.label_camera_permission_denied), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startGallery() {
        resetImageState();

        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(ImageActivity.this, getString(R.string.label_gallery_permission_denied), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        CrashlyticsHelper.log(this.getClass().getSimpleName(), "onActivityResult", "Request code from dialog: " + requestCode
                + " resultCode : " + requestCode + String.format(" Is intent null? : %s ", data == null));
        switch (requestCode) {
            case RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE:
                revokeCameraPermissions();
                handleImageResult(data, RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
                break;
            case RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE:
                handleImageResult(data, RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleImageResult(Intent data, int requestCodes) {
        logMemory();

        Object[] params = new Object[4];
        params[0] = data;
        params[1] = requestCodes;
        params[2] = imageUri;
        params[3] = imagePath;
        new HandleImage().execute(params);
    }

    /**
     * Bitmap and returns the downscaling factor (only sampling every nth pixel) to fit into
     * max width/height.
     * <p>
     * Does not require full bitmap -- decoding just the bounds is sufficient.
     *
     * @param bmp The bitmap to measure
     * @return int Sample factor.
     */
    static private int calcSampleSize(Bitmap bmp) {
        if (bmp != null) {
            float widthRatio = bmp.getWidth() / MAX_IMAGE_WIDTH;
            float heightRatio = bmp.getHeight() / MAX_IMAGE_HEIGHT;
            float largestDimRatio = (widthRatio >= heightRatio) ? widthRatio : heightRatio;
            if (largestDimRatio >= 1) {
                return (int) largestDimRatio;
            }
        }

        return 1;
    }

    /**
     * Loads the image file stored in imagePath, saves a new downscaled version, and resets imageUri
     * and imagePath.
     *
     * @return Result containing a new imagePath and imageUri, pointing to a transformed image.
     */
    @Nullable
    static private ProcessResult processImage(@NonNull Context ctx, int requestCodes, String imagePath, Uri imageUri) {
        if (imagePath == null || imageUri == null)
            return null;

        FileOutputStream outStream = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        {
            // Don't load every pixel if image is too large
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true;
            Bitmap bounds = BitmapFactory.decodeFile(imagePath, boundsOptions);
            options.inSampleSize = calcSampleSize(bounds);
            if (bounds != null)
                if (!bounds.isRecycled()) {
                    bounds.recycle();
                    System.gc();
                }

        }

        try {
            File downscaledFile = createImageFile(ctx);
            outStream = new FileOutputStream(downscaledFile);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            if (bitmap == null) {
                CrashlyticsHelper.log(TAG, "processImage", "Failed to load existing bitmap during downscale");
                return null;
            }

            //Here the orientation is checked and corrected if needed
            bitmap = checkScreenOrientation(imagePath, bitmap, requestCodes);
            System.gc();

            //Here the image is scaled to an acceptable size
            bitmap = ImageScaler.scale(bitmap, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
            System.gc();
            //file is written out
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);

            Log.d(TAG, String.format("Downscaled image dimensions: (%d, %d) %dKB",
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getByteCount() / 1024));

            bitmap.recycle();
            System.gc();

            // New uri and filepath for new downscaled image
            return new ProcessResult(
                    FileProvider.getUriForFile(ctx, Constants.FILE_PROVIDER, downscaledFile),
                    downscaledFile.getAbsolutePath()
            );
        } catch (IOException ex) {
            Toast.makeText(ctx, "Unable to create temporary downscaled image file", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Unable to create temporary downscaled image file", ex);
            CrashlyticsHelper.log(TAG, "processImage", "an IOException");
            CrashlyticsHelper.logException(ex);
        } catch (Throwable ex) {
            CrashlyticsHelper.logException(ex);
        } finally {
            try {
                if (outStream != null)
                    outStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close outstream", e);
                CrashlyticsHelper.logException(e);
            }
        }

        // Exception was thrown
        return null;
    }

    static private File createImageFile(@NonNull Context ctx) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "JPEG_" + timestamp + "_";
        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(filename, ".jpg", storageDir);
    }

    static private Bitmap checkScreenOrientation(String imageP, Bitmap bitmap, int requestCodes) {
        try {
            ExifInterface ei = new ExifInterface(imageP);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);

                case ExifInterface.ORIENTATION_UNDEFINED:
                    if (requestCodes != RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE)
                        bitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }
        } catch (IllegalArgumentException | IOException ia) {
            // If image path is null, IllegalArgumentException is thrown
        }
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        // Image Chooser has reference to this activity. Make sure it doesn't reference a destroyed
        // activity.
        if (imageChooser != null && imageChooser.isShowing())
            imageChooser.dismiss();
        super.onDestroy();
    }

    /**
     * Brute force grant permission to access the URI to every Activity that can respond to
     * ACTION_IMAGE_CAPTURE. Fixes `SecurityException` on API 17, 18 and 19.
     * <p>
     * Since API 19 special permissions are not required and the uri is accessible to any app with
     * the READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE permission.
     * <p>
     * Use this when the camera needs access to a file on the external storage. Paths associated by
     * `external-files-path` and `getExternalFilesDir`.
     *
     * @param intent
     */
    private void grantCameraPermissions(Intent intent) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            List<ResolveInfo> resolvedIntentActivities = getPackageManager()
                    .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities)
                grantUriPermission(resolvedIntentInfo.activityInfo.packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    /**
     * URI Permissions must be revoked, or they will persisted until the device is restarted.
     */
    private void revokeCameraPermissions() {
        if (imageUri == null)
            return;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            revokeUriPermission(imageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    /**
     * Fired when activity result responds with an image uri.
     *
     * @param mediaType The media type of the image file
     * @param imageUri  The uri to the content
     * @param imagePath the file system path to the image
     */
    protected abstract void onImageResult(String mediaType, Uri imageUri, String imagePath);

    /**
     * Image processing is run on a background thread to allow the garbage collector to run. When
     * done on the UI thread, the garbage collector waits for the UI thread to yield.
     */
    private class HandleImage extends AsyncTask<Object, Integer, ImageActivityAsyncTaskResult> {

        /**
         * Guaranteed to run on the UI thread
         *
         * @param result Will contain the new values of imageUri and imagePath
         * @link https://developer.android.com/reference/android/os/AsyncTask.html#onPostExecute(Result)
         */
        @Override
        protected void onPostExecute(ImageActivityAsyncTaskResult result) {
            ContentResolver cR = ImageActivity.this.getContentResolver();
            if (cR != null && result != null) {
                ImageActivity.this.imageUri = result.getImageUri();
                ImageActivity.this.imagePath = result.getImagePath();
                ImageActivity.this.onImageResult(
                        cR.getType(result.getImageUri()),
                        result.getImageUri(),
                        result.getImagePath()
                );
            }
        }

        @Override
        @Nullable
        protected ImageActivityAsyncTaskResult doInBackground(Object... params) {
            Context ctx = ImageActivity.this;

            Intent data = (Intent) params[0];
            int requestCodes = (Integer) params[1];
            Uri imageUri = (Uri) params[2];
            String imagePath = (String) params[3];

            // When the camera is provided with an existing file beforehand, the intent is null. The
            // pre-created image can be found via `imageUri`.
            if (data != null) {
                // No predefined image file was created
                imageUri = data.getData();
                if (imageUri == null) {
                    try {
                        // This is the case where the camera only returns a thumbnail
                        ContentResolver cR = ctx.getContentResolver();
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                        bitmap = checkScreenOrientation(imagePath, bitmap, requestCodes);
                        System.gc();

                        Log.d("IMAGE_TESTS", "Bitmap size : " + bitmap.getByteCount());
                        imageUri = Uri.parse(MediaStore.Images.Media.insertImage(cR, bitmap, "", ""));
                    } catch (Throwable ex) {
                        CrashlyticsHelper.logException(ex);
                    }
                }
            }

            if (TextUtils.isEmpty(imagePath)) {
                // MediaUriHelper does not work when uri points to temp image file
                try {
                    imagePath = MediaUriHelper.getPath(ImageActivity.this, imageUri);
                    CrashlyticsHelper.log(this.getClass().getSimpleName(), "handleImageResult",
                            "Context: " + this + " imageUri :" + imageUri);
                } catch (NullPointerException nullException) {
                    CrashlyticsHelper.logException(nullException);
                }
            }

            ProcessResult result = processImage(ImageActivity.this, requestCodes, imagePath, imageUri);
            if (result != null) {
                imagePath = result.imagePath;
                imageUri = result.imageUri;
            }

            return new ImageActivityAsyncTaskResult(imageUri, imagePath);
        }

        protected void onProgressUpdate(Integer... progress) {

        }
    }

    private static class ProcessResult {
        final Uri imageUri;
        final String imagePath;

        ProcessResult(Uri imageUri, String imagePath) {
            this.imageUri = imageUri;
            this.imagePath = imagePath;
        }
    }
}
