package com.nike.dooit.views.profile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
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
import com.nike.dooit.views.onboarding.ProfileImageActivity;
import com.nike.dooit.views.settings.SettingsActivity;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class ProfileActivity extends DooitActivity {

    private static final String INTENT_MIME_TYPE = "mime_type";
    private static final String INTENT_IMAGE_URI = "image_uri";

    @BindView(R.id.activity_profile_image)
    SimpleDraweeView profileImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Inject
    Persisted persisted;

    @Inject
    FileUploadManager fileUploadManager;

    User user;
    Uri cameraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_d_close_pink);
            getSupportActionBar().setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        user = persisted.getCurrentUser();

        setTitle(user.getUsername());

        profileImage.setImageURI(user.getProfile().getProfileImageUrl());
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) > appBarLayout.getHeight() * 0.4) {
                    isShow = true;
                    profileImage.setVisibility(View.GONE);
                } else if (Math.abs(verticalOffset) < appBarLayout.getHeight() * 0.4) {
                    isShow = false;
                    profileImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        toolbarTitle.setText(titleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsActivity.Builder.create(this).startActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    @OnClick(R.id.activity_profile_image)
    public void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Profile Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utils.checkExternalStoragePermission(ProfileActivity.this);

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
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE);
    }

    void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
        }
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
    private void uploadFromDeviceUri(final Uri cameraUri){
        profileImage.setImageURI(cameraUri);
        ContentResolver cR = this.getContentResolver();
        getIntent().putExtra(INTENT_MIME_TYPE, cR.getType(cameraUri));
        Uri imageUri = getRealPathFromURI(cameraUri);
        getIntent().putExtra(INTENT_IMAGE_URI, imageUri);
        User user = persisted.getCurrentUser();
        fileUploadManager.upload(user.getId(), getIntent().getStringExtra(INTENT_MIME_TYPE), new File(imageUri.getPath()), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(ProfileActivity.this, "Unable to uploadProfileImage Image", Toast.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                User user = persisted.getCurrentUser();
                user.getProfile().setProfileImageUrl(cameraUri.toString());
                persisted.setCurrentUser(user);
            }
        });
    }
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            cameraUri = data.getData();
            uploadFromDeviceUri(data.getData());
        }
    }

    //@OnActivityResult(requestCode = RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE)
    void onActivityResultCameraProfileImage(Intent data) {
        cameraUri = data.getData();
        if (cameraUri == null) {
            try {
                cameraUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), (Bitmap) data.getExtras().get("data"), "", ""));
            } catch (Throwable ex) {

            }
        }
        uploadFromDeviceUri(cameraUri);

    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static Builder create(Context context) {
            Builder builder = new Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ProfileActivity.class);
        }

    }
}
