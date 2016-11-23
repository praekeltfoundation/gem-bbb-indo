package com.nike.dooit.views.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.helpers.activity.result.ActivityForResultHelper;
import com.nike.dooit.models.User;
import com.nike.dooit.views.DooitActivity;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;
import com.nike.dooit.views.main.adapters.MainTabAdapter;
import com.nike.dooit.views.profile.ProfileActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends DooitActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content_main_view_pager)
    ViewPager viewPager;

    @BindView(R.id.content_main_tab_layout)
    TabLayout tabLayout;

    MainTabAdapter mainTabAdapter;
    @Inject
    ActivityForResultHelper activityForResultHelper;

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Remember to remove it if you don't want it to fire every time
                    MainActivity.this.toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int abHeight = (int)(getSupportActionBar().getHeight()*0.7);
                    try {
                        User user = persisted.getCurrentUser();
                        Uri profileImageUri = Uri.parse(user.getProfile().getProfileImageUrl());
                        InputStream inputStream = getContentResolver().openInputStream(profileImageUri);
                        Bitmap squareProfileImage = BitmapFactory.decodeStream(inputStream);
                        int scaledWidth = (squareProfileImage.getWidth()*abHeight)/squareProfileImage.getHeight();
                        Bitmap squareScaledProfileImage = Bitmap.createScaledBitmap(squareProfileImage, scaledWidth, abHeight, false);
                        RoundedBitmapDrawable roundedProfileImage = RoundedBitmapDrawableFactory.create(getResources(),squareScaledProfileImage);
                        roundedProfileImage.setCircular(true);

                        getSupportActionBar().setHomeAsUpIndicator(roundedProfileImage);
                    } catch (Exception e) {
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_d_profile);
                    }

                }
            });
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_d_profile);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProfile();
                }
            });
        }
        mainTabAdapter = new MainTabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mainTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mainTabAdapter.getTabView(i));
        }
    }

    private void openProfile() {
        ProfileActivity.Builder.create(this).startActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!activityForResultHelper.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static class Builder extends DooitActivityBuilder<MainActivity.Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static MainActivity.Builder create(Context context) {
            MainActivity.Builder builder = new MainActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, MainActivity.class);
        }

    }


}
