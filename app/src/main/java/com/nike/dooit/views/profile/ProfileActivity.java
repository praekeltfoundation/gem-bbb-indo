package com.nike.dooit.views.profile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.FileUploadManager;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.helpers.permissions.PermissionCallback;
import com.nike.dooit.helpers.permissions.PermissionsHelper;
import com.nike.dooit.models.User;
import com.nike.dooit.util.Persisted;
import com.nike.dooit.util.RequestCodes;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;
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
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @Inject
    Persisted persisted;

    @Inject
    FileUploadManager fileUploadManager;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_invert);
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
    }

    @Override
    public void setTitle(CharSequence title) {
        collapsingToolbarLayout.setTitle(title);
        super.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
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
    void takeImage() {
        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                permissionsHelper.askForPermission(ProfileActivity.this, PermissionsHelper.D_CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
                        }
                    }

                    @Override
                    public void permissionRefused() {
                        Toast.makeText(ProfileActivity.this, "Can't take profile image without camera permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(ProfileActivity.this, "Can't take profile image without storage permission", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        switch (requestCode) {
            case RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE:
                onActivityResultCameraProfileImage(data);
                break;
        }
        //  ActivityResult.onResult(requestCode, resultCode, data).into(this);

    }

    //@OnActivityResult(requestCode = RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE)
    void onActivityResultCameraProfileImage(Intent data) {
        Uri cameraUri = data.getData();
        if (cameraUri == null) {
            try {
                cameraUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), (Bitmap) data.getExtras().get("data"), "", ""));

            } catch (Throwable ex) {

            }
        }
        getIntent().putExtra(INTENT_IMAGE_URI, cameraUri);
        ContentResolver cR = this.getContentResolver();
        getIntent().putExtra(INTENT_MIME_TYPE, cR.getType(cameraUri));
        profileImage.setImageURI(cameraUri);
        User user = persisted.getCurrentUser();
        fileUploadManager.upload(user.getId(), getIntent().getStringExtra(INTENT_MIME_TYPE), new File(((Uri) getIntent().getParcelableExtra(INTENT_IMAGE_URI)).toString()), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(ProfileActivity.this, "Unable to uploadProfileImage Image", Toast.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
            }
        });
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
