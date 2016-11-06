package com.nike.dooit.views.onboarding;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.managers.FileUploadManager;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.models.User;
import com.nike.dooit.util.Persisted;
import com.nike.dooit.util.RequestCodes;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;
import com.nike.dooit.views.main.MainActivity;

import java.io.File;

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
    public void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
        }
    }

    @OnClick(R.id.activity_profile_image_next_button)
    public void uploadProfileImage() {
        User user = persisted.getCurrentUser();
        fileUploadManager.upload(user.getId(), getIntent().getStringExtra(INTENT_MIME_TYPE), new File(((Uri) getIntent().getParcelableExtra(INTENT_IMAGE_URI)).toString()), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(ProfileImageActivity.this, "Unable to upload Image", Toast.LENGTH_SHORT).show();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                MainActivity.Builder.create(ProfileImageActivity.this).startActivity();
            }
        });
    }

    @OnClick(R.id.activity_profile_image_skip_text_view)
    public void skip() {
        MainActivity.Builder.create(ProfileImageActivity.this).startActivity();

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

    // @OnActivityResult(requestCode = RequestCodes.REPONSE_CAMERA_REQUEST_PROFILE_IMAGE)
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
        simpleDraweeView.setImageURI(cameraUri);
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
