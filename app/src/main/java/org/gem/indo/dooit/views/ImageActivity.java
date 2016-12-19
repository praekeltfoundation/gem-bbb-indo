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

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.ImageChooserOptions;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.helpers.RequestCodes;

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

    }

    private void startGallery() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                return;
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
