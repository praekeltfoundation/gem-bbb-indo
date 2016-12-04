package org.gem.indo.dooit.views.profile;

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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.AchievementManager;
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.responses.AchievementResponse;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.MediaUriHelper;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.permissions.PermissionCallback;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.profile.adapters.BadgeAdapter;
import org.gem.indo.dooit.views.settings.SettingsActivity;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class ProfileActivity extends DooitActivity {

    private static final String INTENT_MIME_TYPE = "mime_type";
    private static final String INTENT_IMAGE_URI = "image_uri";
    private static final float MIN_PROFILE_IMAGE_SCALE = 0.4f;

    @BindView(R.id.activity_profile_scroll_view)
    View background;

    @BindView(R.id.activity_profile_image)
    SimpleDraweeView profileImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.profile_current_streak_value)
    TextView streakView;

    @BindView(R.id.activity_profile_achievement_recycler_view)
    RecyclerView achievementRecyclerView;

    @BindString(R.string.profile_week_streak_singular)
    String streakSingular;

    @BindString(R.string.profile_week_streak_plural)
    String streakPlural;

    @Inject
    Persisted persisted;

    @Inject
    FileUploadManager fileUploadManager;

    @Inject
    AchievementManager achievementManager;

    private User user;
    private Uri imageUri;
    private BadgeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_profile);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        // Appbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(org.gem.indo.dooit.R.drawable.ic_d_close_pink);
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

        // Profile image collapse
        profileImage.setImageURI(user.getProfile().getProfileImageUrl());
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) > appBarLayout.getHeight() * 0.4) {
                    isShow = true;
                    if (profileImage.getVisibility() == View.VISIBLE)
                        profileImage.setVisibility(View.GONE);
                } else if (Math.abs(verticalOffset) < appBarLayout.getHeight() * 0.4) {
                    isShow = false;
                    if (profileImage.getVisibility() == View.GONE)
                        profileImage.setVisibility(View.VISIBLE);
                    float scale = 1 - ((float) Math.abs(verticalOffset) / (appBarLayout.getHeight() * 0.4f));
                    profileImage.setScaleX(Math.max(scale, MIN_PROFILE_IMAGE_SCALE));
                    profileImage.setScaleY(Math.max(scale, MIN_PROFILE_IMAGE_SCALE));
                }
            }
        });

        // Achievevments
        setStreak(0);

        adapter = new BadgeAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration separator = new DividerItemDecoration(this, manager.getOrientation());
        achievementRecyclerView.setLayoutManager(manager);
        achievementRecyclerView.addItemDecoration(separator);
        achievementRecyclerView.setAdapter(adapter);

        achievementManager.retrieveAchievement(user.getId(), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(ProfileActivity.this, "Error retrieving achievements", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProfileActivity.this.hideProgress();
                    }
                });
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                    }
                });
            }
        }).subscribe(new Action1<AchievementResponse>() {
            @Override
            public void call(final AchievementResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStreak(response.getWeeklyStreak());
                        adapter.addAll(response.getBadges());
                    }
                });
            }
        });

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, appBarLayout);
        // FIXME: Setting the purple background on the appbar tints the grey background pattern purple as well.
//        SquiggleBackgroundHelper.setBackground(this, R.color.grey_back, R.color.grey_fore, background);

    }

    @Override
    protected void onResume() {
        super.onResume();
        user = persisted.getCurrentUser();
        setTitle(user.getUsername());
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
        getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_profile, menu);
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
        final CharSequence[] items = {
                getString(R.string.profile_image_camera),
                getString(R.string.profile_image_gallery),
                getString(R.string.profile_image_cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Profile Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO: Use Enums or Constants
                switch (item) {
                    case 0:
                        startCamera();
                        break;
                    case 1:
                        startGallery();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    protected void startCamera() {
        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                permissionsHelper.askForPermission(ProfileActivity.this, PermissionsHelper.D_CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE);
                        }
                    }

                    @Override
                    public void permissionRefused() {
                        Toast.makeText(ProfileActivity.this, "Can't take ic_d_profile image without camera permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(ProfileActivity.this, "Can't take ic_d_profile image without storage permission", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void startGallery() {
        permissionsHelper.askForPermission(this, PermissionsHelper.D_WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(ProfileActivity.this, "Can't take ic_d_profile image without storage permission", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        switch (requestCode) {
            case RequestCodes.RESPONSE_CAMERA_REQUEST_PROFILE_IMAGE:
                onActivityImageResult(data);
                break;
            case RequestCodes.RESPONSE_GALLERY_REQUEST_PROFILE_IMAGE:
                onActivityImageResult(data);
                break;
        }
    }

    protected void onActivityImageResult(Intent data) {
        imageUri = data.getData();
        if (imageUri == null) {
            try {
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), (Bitmap) data.getExtras().get("data"), "", ""));
            } catch (Throwable ex) {

            }
        }
        profileImage.setImageURI(imageUri);
        ContentResolver cR = this.getContentResolver();
        uploadImage(cR.getType(imageUri), MediaUriHelper.getPath(this, imageUri));
    }

    protected void uploadImage(String mimetype, String filepath) {
        User user = persisted.getCurrentUser();
        showProgressDialog(R.string.profile_image_progress);
        fileUploadManager.upload(user.getId(), mimetype, new File(filepath), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(ProfileActivity.this, "Unable to uploadProfileImage Image", Toast.LENGTH_SHORT).show();
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                dismissDialog();
            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                User user = persisted.getCurrentUser();

                // Clear remote image from Fresco cache
                Uri currentUri = Uri.parse(user.getProfile().getProfileImageUrl());
                if (currentUri.getScheme().equals("http") || currentUri.getScheme().equals("https")) {
                    ImagePipeline pipeline = Fresco.getImagePipeline();
                    pipeline.evictFromCache(currentUri);
                }
            }
        });
    }

    protected void setStreak(int streak) {
        if (streak == 1)
            streakView.setText(String.format(streakSingular, streak));
        else
            streakView.setText(String.format(streakPlural, streak));
    }

    protected void hideProgress() {
        View view = this.findViewById(R.id.achievements_progress_container);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
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
