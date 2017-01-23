package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AuthenticationManager;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.crashlytics.crashlyticsHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.ImageActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.MainActivity;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.functions.Action1;

public class ProfileImageActivity extends ImageActivity {

    @BindView(R.id.activity_profile_image)
    View background;

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

    // Image upload is only performed on button pressed, not activity result return
    private String mediaType;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_profile_image);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }

    @Override
    public void onBackPressed() {

    }

    @OnClick(R.id.activity_profile_image_profile_image)
    public void selectImage() {
        showImageChooser();
    }

    @Override
    protected void onImageResult(String mediaType, Uri imageUri, String imagePath) {
        simpleDraweeView.setImageURI(imageUri);
        this.mediaType = mediaType;
        this.imagePath = imagePath;
    }

    @OnClick(R.id.activity_profile_image_next_button)
    public void uploadProfileImage() {
        // Image must be set
        if (TextUtils.isEmpty(mediaType) || TextUtils.isEmpty(imagePath)) {
            Snackbar.make(nextButton, getString(R.string.profile_image_empty_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        User user = persisted.getCurrentUser();

        fileUploadManager.uploadProfileImage(user.getId(), mediaType,
                new File(imagePath), new DooitErrorHandler() {
                    @Override
                    public void onError(DooitAPIError error) {
                        for (String msg : error.getErrorMessages())
                            Snackbar.make(nextButton, msg, Snackbar.LENGTH_SHORT).show();
                    }
                }).subscribe(new Action1<Response<EmptyResponse>>() {
            @Override
            public void call(Response<EmptyResponse> response) {
                User user = persisted.getCurrentUser();
                try{
                    user.getProfile().setProfileImageUrl(getImageUri().toString());
                }
                catch (NullPointerException nullException){
                    crashlyticsHelper.log(this.getClass().getSimpleName(),"uploadProfileImage :",String.format("User is null %s: ",
                            user == null) + " Uri :" + getImageUri());
                }

                persisted.setCurrentUser(user);
                MainActivity.Builder.create(ProfileImageActivity.this).startActivityClearTop();
            }
        });
    }

    @OnClick(R.id.activity_profile_image_skip_text_view)
    public void skip() {
        MainActivity.Builder.create(ProfileImageActivity.this).startActivityClearTop();
    }

    public static class Builder extends DooitActivityBuilder<Builder> {

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
