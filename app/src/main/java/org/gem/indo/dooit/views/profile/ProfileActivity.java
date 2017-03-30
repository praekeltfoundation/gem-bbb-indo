package org.gem.indo.dooit.views.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import org.gem.indo.dooit.api.managers.FileUploadManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.ImageActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.profile.adapters.ProfileTabAdapter;
import org.gem.indo.dooit.views.profile.enums.ProfileTabPosition;
import org.gem.indo.dooit.views.settings.SettingsActivity;

import java.io.File;

import javax.inject.Inject;

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

    @BindView(R.id.activity_profile)
    View background;

    @BindView(R.id.activity_profile_image)
    SimpleDraweeView profileImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.activity_profile_tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.activity_profile_view_pager)
    ViewPager viewPager;

    @Inject
    Persisted persisted;

    @Inject
    FileUploadManager fileUploadManager;

    private ProfileTabAdapter tabsAdapter;
    private User user;
    private Uri imageUri;

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
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) > appBarLayout.getHeight() * 0.4) {
//                    Log.d("OFFSET", String.format("verticleOffset: %d ; height: %f ; show: false", verticalOffset, appBarLayout.getHeight() * 0.4));
                    if (profileImage.getVisibility() == View.VISIBLE)
                        profileImage.setVisibility(View.INVISIBLE);
                } else if (Math.abs(verticalOffset) < appBarLayout.getHeight() * 0.4) {
//                    Log.d("OFFSET", String.format("verticleOffset: %d ; height: %f ; show: true", verticalOffset, appBarLayout.getHeight() * 0.4));
                    if (profileImage.getVisibility() == View.INVISIBLE)
                        profileImage.setVisibility(View.VISIBLE);
                    float scale = 1 - ((float) Math.abs(verticalOffset) / (appBarLayout.getHeight() * 0.4f));
                    profileImage.setScaleX(Math.max(scale, MIN_PROFILE_IMAGE_SCALE));
                    profileImage.setScaleY(Math.max(scale, MIN_PROFILE_IMAGE_SCALE));
                }
            }
        });

        // Tabbed view pager
        tabsAdapter = new ProfileTabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null)
                tab.setCustomView(tabsAdapter.createTabView(tabLayout, i));
        }

        // Default Tab
        viewPager.setCurrentItem(ProfileTabPosition.ACHIEVEMENTS.getId());

        // Background
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, appBarLayout);
        // FIXME: Setting the purple background on the appbar tints the grey background pattern purple as well.
        SquiggleBackgroundHelper.setBackground(this, R.color.grey_back, R.color.grey_fore, background, true);

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
        CrashlyticsHelper.log(this.getClass().getSimpleName(), "OnClick select image : ", "Tap to change profile image (Settings)");
        showImageChooser();
    }

    @Override
    protected void onImageResult(String mediaType, Uri imageUri, String imagePath) {
        CrashlyticsHelper.log(this.getClass().getSimpleName(), "OnImageResult : ", "successful image result (settings)");
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
            intent = new Intent(context, ProfileActivity.class);
            return intent;
        }
    }
}
