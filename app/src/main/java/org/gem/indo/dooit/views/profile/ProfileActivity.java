package org.gem.indo.dooit.views.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import org.gem.indo.dooit.helpers.Crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.ImageActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.profile.adapters.BadgeAdapter;
import org.gem.indo.dooit.views.settings.SettingsActivity;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Bernhard Müller on 2016/07/22.
 */
public class ProfileActivity extends ImageActivity {

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
        setContentView(R.layout.activity_profile);
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
        if (user.hasProfileImage())
            DraweeHelper.setProgressiveUri(profileImage, Uri.parse(user.getProfile().getProfileImageUrl()));
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

        // Achievements
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
        if (user == null) {
            Snackbar.make(toolbar, R.string.prompt_relogin, Snackbar.LENGTH_SHORT);
            return;
        }
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
        CrashlyticsHelper.log(this.getClass().getSimpleName(),"OnClick select image : ", "Tap to change profile image (Settings)");
        showImageChooser();
    }

    @Override
    protected void onImageResult(String mediaType, Uri imageUri, String imagePath) {
        CrashlyticsHelper.log(this.getClass().getSimpleName(),"OnImageResult : ", "successful image result (settings)");
        // Upload image to server
        User user = persisted.getCurrentUser();
        if (user == null) {
            Snackbar.make(toolbar, R.string.prompt_relogin, Snackbar.LENGTH_SHORT);
            return;
        }

        showProgressDialog(R.string.profile_image_progress);

        fileUploadManager.uploadProfileImage(user.getId(), mediaType, new File(imagePath), new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                //hardcoded string
                Toast.makeText(ProfileActivity.this, "Unable to uploadProfileImage Image", Toast.LENGTH_SHORT).show();
            }
        }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                dismissDialog();
            }
        }).subscribe(new Action1<Response<EmptyResponse>>() {
            @Override
            public void call(Response<EmptyResponse> response) {
                User user = persisted.getCurrentUser();

                if (user.hasProfileImage()) {
                    // Clear existing remote image from Fresco cache
                    Uri currentUri = Uri.parse(user.getProfile().getProfileImageUrl());
                    if (currentUri.getScheme().equals("http") || currentUri.getScheme().equals("https")) {
                        ImagePipeline pipeline = Fresco.getImagePipeline();
                        pipeline.evictFromCache(currentUri);
                    }
                }

                String location = response.headers().get("Location");
                if (!TextUtils.isEmpty(location))
                    user.getProfile().setProfileImageUrl(location);

                persisted.setCurrentUser(user);

                // Display image in view from content uri so we don't have to redownload it just yet
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileImage.setImageURI(getImageUri());
                    }
                });
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
