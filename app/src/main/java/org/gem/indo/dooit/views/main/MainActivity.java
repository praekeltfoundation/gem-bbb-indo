package org.gem.indo.dooit.views.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultHelper;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.adapters.MainTabAdapter;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.target.TargetFragment;
import org.gem.indo.dooit.views.profile.ProfileActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class MainActivity extends DooitActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_main_profile_image)
    SimpleDraweeView simpleDraweeViewProfile;

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
        setContentView(org.gem.indo.dooit.R.layout.activity_main);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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

        // App opened from notification. Direct to appropriate screen.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            if (extras.containsKey(NotificationType.NOTIFICATION_TYPE))
                switch (NotificationType.getValueOf(extras.getInt(NotificationType.NOTIFICATION_TYPE))) {
                    case CHALLENGE_AVAILABLE:
                        startPage(MainViewPagerPositions.CHALLENGE);
                }

        // Set alarm for when the app opens without going through registration or login
        NotificationAlarm.setAlarm(this);
    }

    @OnPageChange(value = R.id.content_main_view_pager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onMainPagerPageSelected(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View tabView = tab.getCustomView();

            if (position == i) {
                MainViewPagerPositions.setActiveState(tabView);

            } else {
                MainViewPagerPositions.setInActiveState(tabView);

            }

        }
    }

    public void startPage(MainViewPagerPositions pos) {
        viewPager.setCurrentItem(pos.getValue(), true);
    }

    @OnClick(R.id.activity_main_profile_image)
    void openProfile() {
        ProfileActivity.Builder.create(this).startActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleDraweeViewProfile.setImageURI(persisted.getCurrentUser().getProfile().getProfileImageUrl());
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

    public MainFragment getFragment(MainViewPagerPositions MainViewPagerPositions) {
        return ((MainFragment) mainTabAdapter.instantiateItem(viewPager, MainViewPagerPositions.getValue()));
    }

    public void refreshGoals() {
        ((TargetFragment) getFragment(MainViewPagerPositions.TARGET)).refreshGoals();
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
