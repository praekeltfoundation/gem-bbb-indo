package org.gem.indo.dooit.views;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.RequestCodes;
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

/**
 * Created by Wimpie Victor on 2016/12/19.
 */

public abstract class ImageActivity extends DooitActivity {

    private static final String TAG = ImageActivity.class.getName();
    // Same as `authorities` attribute in manifest
    private static final int maxImageWidth = 1024;
    private static final int maxImageHeight = 1024;

    private AlertDialog imageChooser;
    private Uri imageUri;
    private String imagePath;

    private void resetImageState() {
        imageUri = null;
        imagePath = null;
    }

    protected void showImageChooser() {
        resetImageState();
        final CharSequence[] items = ImageChooserOptions.createMenuItems(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
        builder.setTitle(getString(R.string.label_image_chooser_title));

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (ImageChooserOptions.valueOf(which)) {
                    case CAMERA:
                        startCamera();
                        break;
                    case GALLERY:
                        startGallery();
                        break;
                    case CANCEL:
                        dialog.dismiss();
                        break;
                }
            }
        });

        imageChooser = builder.show();
    }

    private void startCamera() {
        permissionsHelper.askForPermission(this, new String[]{PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, PermissionsHelper.D_CAMERA}, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // The file where the full sized photo will be temporarily stored. When this is not
                // done, we would get the thumbnail back instead. See documentation:
                // https://developer.android.com/training/camera/photobasics.html#TaskPath
                File imageFile = null;

                try {
                    imageFile = createImageFile();
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

    private void startGallery() {
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

        switch (requestCode) {
            case RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE:
                revokeCameraPermissions();
            case RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE:
                handleImageResult(data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleImageResult(Intent data) {
        // When the camera is provided with an existing file beforehand, the intent is null. The
        // pre-created image can be found via `imageUri`.
        if (data != null) {
            // No predefined image file was created
            imageUri = data.getData();
            if (imageUri == null) {
                try {
                    // This is the case where the camera only returns a thumbnail
                    ContentResolver cR = this.getContentResolver();
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    Log.d("IMAGE_TESTS", "Bitmap size : " + bm.getByteCount());
                    imageUri = Uri.parse(MediaStore.Images.Media.insertImage(cR, bm, "", ""));
                } catch (Throwable ex) {

                }
            }
        }

        if (TextUtils.isEmpty(imagePath))
            // MediaUriHelper does not work when uri points to temp image file
            imagePath = MediaUriHelper.getPath(this, imageUri);

        downscaleImage();

        ContentResolver cR = this.getContentResolver();
        onImageResult(cR.getType(imageUri), imageUri, imagePath);
    }

    /**
     * Loads the image file stored in imagePath, saves a new downscaled version, and resets imageUri
     * and imagePath.
     */
    private void downscaleImage() {
        FileOutputStream outstream = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            File downscaledFile = createImageFile();
            outstream = new FileOutputStream(downscaledFile);
            Bitmap downscaledBitmap = null;
            Bitmap existingBitmap = BitmapFactory.decodeFile(imagePath, options);

            if (existingBitmap == null) {
                Log.e(TAG, "Failed to load existing bitmap during downscale");
                return;
            }

            Log.d(TAG, String.format("Existing image dimensions: (%d, %d) %dKB",
                    existingBitmap.getWidth(), existingBitmap.getHeight(), existingBitmap.getByteCount() / 1024));

            downscaledBitmap = ImageScaler.scale(existingBitmap, maxImageWidth, maxImageHeight);
            downscaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outstream);

            Log.d(TAG, String.format("Downscaled image dimensions: (%d, %d) %dKB",
                    downscaledBitmap.getWidth(), downscaledBitmap.getHeight(), downscaledBitmap.getByteCount() / 1024));

            // Set uri and filepath to new downscaled image
            imagePath = downscaledFile.getAbsolutePath();
            imageUri = FileProvider.getUriForFile(this, Constants.FILE_PROVIDER, downscaledFile);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to create temporary downscaled image file", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Unable to create temporary downscaled image file", e);
        } finally {
            try {
                if (outstream != null)
                    outstream.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close outstream", e);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(filename, ".jpg", storageDir);

        return image;
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
     *
     * Since API 19 special permissions are not required and the uri is accessible to any app with
     * the READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE permission.
     *
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
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            revokeUriPermission(imageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    protected Uri getImageUri() {
        return imageUri;
    }

    /**
     * Fired when activity result responds with an image uri.
     *
     * @param mediaType The media type of the image file
     * @param imageUri  The uri to the content
     * @param imagePath the file system path to the image
     */
    protected abstract void onImageResult(String mediaType, Uri imageUri, String imagePath);
}
