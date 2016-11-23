package com.nike.dooit.views.onboarding;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.managers.FileUploadManager;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.helpers.Utils;
import com.nike.dooit.helpers.permissions.PermissionCallback;
import com.nike.dooit.helpers.permissions.PermissionsHelper;
import com.nike.dooit.models.User;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.helpers.RequestCodes;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;
import com.nike.dooit.views.main.MainActivity;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class ProfileImageActivity extends DooitActivity {

    private static final String INTENT_MIME_TYPE = "mime_type";
    private static final String INTENT_IMAGE_URI = "image_uri";

    @BindView(R.id.activity_profile_image_profile_image)
    SimpleDraweeView simpleDraweeView;

    @BindView(R.id.activity_profile_image_next_button)
    Button nextButton;

    @Inject
    AuthenticationManager authenticationManager;

    @Inject
    FileUploadManager fileUploadManager;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {

    }

    @OnClick(R.id.activity_profile_image_profile_image)
    public void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileImageActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utils.checkExternalStoragePermission(ProfileImageActivity.this);

                if (items[item].equals("Take Photo")) {
                    if(result)
                        takeImage();

                } else if (items[item].equals("Choose from Gallery")) {
                    if(result)
                        chooseImage();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE);
    }

    @OnClick(R.id.activity_profile_image_next_button)
    public void uploadProfileImage() {
        if (!getIntent().hasExtra(INTENT_IMAGE_URI)) {
            Toast.makeText(this, "Upload image of click Skip", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = persisted.getCurrentUser();
        fileUploadManager.upload(user.getId(), getIntent().getStringExtra(INTENT_MIME_TYPE),
                new File(((Uri) getIntent().getParcelableExtra(INTENT_IMAGE_URI)).toString()), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                for (String msg : error.getErrorMessages())
                    Snackbar.make(nextButton, msg, Snackbar.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                User user = persisted.getCurrentUser();
                user.getProfile().setProfileImageUrl(cameraUri.toString());
                persisted.setCurrentUser(user);
                MainActivity.Builder.create(ProfileImageActivity.this).startActivityClearTop();
            }
        });
    }

    @OnClick(R.id.activity_profile_image_skip_text_view)
    public void skip() {
        MainActivity.Builder.create(ProfileImageActivity.this).startActivityClearTop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        switch (requestCode) {
            case RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE:
                onActivityResultCameraProfileImage(data);
                break;
            case RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE:
                onSelectFromGalleryResult(data);
                break;
        }
        //  ActivityResult.onResult(requestCode, resultCode, data).into(this);

    }

    Uri cameraUri;
    // @OnActivityResult(requestCode = RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE)
    void onActivityResultCameraProfileImage(Intent data) {
        cameraUri = data.getData();
        if (cameraUri == null) {
            try {
                cameraUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), (Bitmap) data.getExtras().get("data"), "", ""));
            } catch (Throwable ex) {

            }
        }
        ContentResolver cR = this.getContentResolver();
        simpleDraweeView.setImageURI(cameraUri);
        getIntent().putExtra(INTENT_MIME_TYPE, cR.getType(cameraUri));
        getIntent().putExtra(INTENT_IMAGE_URI, getRealPathFromURI(cameraUri));
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            cameraUri = data.getData();
            ContentResolver cR = this.getContentResolver();
            simpleDraweeView.setImageURI(cameraUri);
            getIntent().putExtra(INTENT_MIME_TYPE, cR.getType(cameraUri));
            getIntent().putExtra(INTENT_IMAGE_URI, getRealPathFromURI(cameraUri));
        }
    }

    public static class Builder extends DooitActivityBuilder<ProfileImageActivity.Builder> {

        protected Builder(Context context) {
            super(context);
        }

        public static ProfileImageActivity.Builder create(Context context) {
            ProfileImageActivity.Builder builder = new ProfileImageActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ProfileImageActivity.class);
        }

    }
}
