package org.gem.indo.dooit.views.main;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultHelper;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.adapters.MainTabAdapter;
import org.gem.indo.dooit.views.profile.ProfileActivity;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

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

    private InputStream resolveInputStream(Uri streamUri) throws IOException {
        HttpURLConnection urlConnection = null;
        try {

            URL remote = new URL(streamUri.toString());
            urlConnection = (HttpURLConnection) remote.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization","Token " + persisted.getToken());
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            return in;
        }catch (Exception ioe){
            Crashlytics.log("cannot download " + streamUri);

        }
        return getContentResolver().openInputStream(streamUri);

    }
    private class ProfileImageTask extends AsyncTask<Void,Integer,RoundedBitmapDrawable>{

        @Override
        protected RoundedBitmapDrawable doInBackground(Void... params) {

            int abHeight = (int) (getSupportActionBar().getHeight() * 0.7);
            try {
                User user = persisted.getCurrentUser();
                Uri profileImageUri = Uri.parse(user.getProfile().getProfileImageUrl());
                InputStream inputStream = resolveInputStream(profileImageUri);
                Bitmap squareProfileImage = BitmapFactory.decodeStream(inputStream);
                int scaledWidth = (squareProfileImage.getWidth() * abHeight) / squareProfileImage.getHeight();
                Bitmap squareScaledProfileImage = Bitmap.createScaledBitmap(squareProfileImage, scaledWidth, abHeight, false);
                RoundedBitmapDrawable roundedProfileImage = RoundedBitmapDrawableFactory.create(getResources(), squareScaledProfileImage);
                roundedProfileImage.setCircular(true);

                return roundedProfileImage;
            } catch (Exception e) {
                Crashlytics.log("cannot scale profile image " + e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(RoundedBitmapDrawable roundedProfileImage){
            if(roundedProfileImage!=null)
                getSupportActionBar().setHomeAsUpIndicator(roundedProfileImage);
            else
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_d_profile);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.gem.indo.dooit.R.layout.activity_main);
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
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_d_profile);
                    new ProfileImageTask().execute();

                }
            });
            getSupportActionBar().setHomeAsUpIndicator(org.gem.indo.dooit.R.drawable.ic_d_profile);

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

            if (i == 0) {
                MainViewPagerPositions.setActiveState(tab.getCustomView());
            } else {
                MainViewPagerPositions.setInActiveState(tab.getCustomView());
            }
        }
        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for (int i = 0; i < tabLayout.getTabCount(); i++) {
                            TabLayout.Tab tab = tabLayout.getTabAt(i);
                            View tabView = tab.getCustomView();
                            ImageView icon = (ImageView) tabView.findViewById(R.id.tab_custom_icon);
                            TextView text = (TextView) tabView.findViewById(R.id.tab_custom_title);

                            if (position == i) {
                                MainViewPagerPositions.setActiveState(tabView);

                            } else {
                                MainViewPagerPositions.setInActiveState(tabView);

                            }

                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }

        );
    }

    private void openProfile() {
        ProfileActivity.Builder.create(this).startActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main, menu);
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

    public static class Builder extends DooitActivityBuilder<Builder> {
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
