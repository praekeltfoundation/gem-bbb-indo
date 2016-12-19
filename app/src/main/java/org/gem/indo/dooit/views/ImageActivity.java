package org.gem.indo.dooit.views;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.ImageChooserOptions;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.permissions.PermissionCallback;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;
import org.gem.indo.dooit.views.profile.ProfileActivity;

/**
 * Created by Wimpie Victor on 2016/12/19.
 */

public abstract class ImageActivity extends DooitActivity {

    private AlertDialog imageChooser;
    private Uri imageUri;

    protected void showImageChooser() {
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
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
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
            case RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE:
                handleImageResult(data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleImageResult(Intent data) {
        imageUri = data.getData();
        if (imageUri == null) {
            try {
                ContentResolver cR = this.getContentResolver();
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                Log.d("IMAGE_TESTS", "Bitmap size : " + bm.getByteCount());
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(cR, bm, "", ""));

            } catch (Throwable ex) {

            }
        }
        ContentResolver cR = this.getContentResolver();
        onImageResult(cR.getType(imageUri), imageUri, MediaUriHelper.getPath(this, imageUri));
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
     * Fired when activity result responds with an image uri.
     *
     * @param mediaType The media type of the image file
     * @param imageUri  The uri to the content
     * @param imagePath the file system path to the image
     */
    protected abstract void onImageResult(String mediaType, Uri imageUri, String imagePath);
}
