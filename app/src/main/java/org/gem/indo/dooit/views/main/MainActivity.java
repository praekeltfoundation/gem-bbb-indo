package org.gem.indo.dooit.views.main;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.Tracker;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultHelper;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.services.NotificationArgs;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.adapters.MainTabAdapter;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.bot.BotFragment;
import org.gem.indo.dooit.views.main.fragments.target.TargetFragment;
import org.gem.indo.dooit.views.profile.ProfileActivity;

import java.util.Stack;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

import static android.view.View.GONE;
import static org.gem.indo.dooit.views.main.MainViewPagerPositions.BOT;
import static org.gem.indo.dooit.views.main.MainViewPagerPositions.TARGET;

public class MainActivity extends DooitActivity {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_main_profile_image)
    SimpleDraweeView simpleDraweeViewProfile;

    @BindView(R.id.content_main_view_pager)
    ViewPager viewPager;

    @BindView(R.id.content_main_tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.toolbar_title)
    TextView titleView;

    @BindView(R.id.toolbar_logo_dooit_image_view)
    ImageView dooitIcon;

    @BindString(R.string.main_tab_text_0)
    String BINA;

    @BindString(R.string.main_tab_text_1)
    String GOALS;

    @BindString(R.string.main_tab_text_2)
    String CHALLENGE;

    @BindString(R.string.main_tab_text_3)
    String TIPS;

    MainTabAdapter mainTabAdapter;

    @Inject
    ActivityForResultHelper activityForResultHelper;

    @Inject
    Persisted persisted;

    @Inject
    Tracker tracker;

    private Stack<MainViewPagerPositions> pageHistory;
    private int currentPos;
    // Guard flag to prevent pushing to history when pressing back
    private boolean saveToHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        // Clear bot state
        persisted.clearConversation();
        persisted.clearConvoGoals();
        persisted.clearConvoTip();

        setSupportActionBar(toolbar);
        dooitIcon.setVisibility(View.GONE);
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
        if (extras != null && extras.containsKey(NotificationType.NOTIFICATION_TYPE)) {
            switch (NotificationType.getValueOf(extras.getInt(NotificationType.NOTIFICATION_TYPE))) {
                case CHALLENGE_AVAILABLE:
                    startPage(MainViewPagerPositions.CHALLENGE);
                    break;
                case SAVING_REMINDER:
                    startPage(MainViewPagerPositions.TARGET);
                    break;
                case SURVEY_AVAILABLE:
                    handleSurveyNotification();
                    break;
            }
        }

        // Set alarm for when the app opens without going through registration or login
        NotificationAlarm.setAlarm(this);

        // ViewPager history functionality
        pageHistory = new Stack<>();
        currentPos = 0;
        saveToHistory = true;

        /*Set the ActionBar's title for Bina here to ensure it gets set initially
        When the app starts up for a logged in user technically no page has been selected*/
        onMainPagerPageSelected(currentPos);
    }

    @OnPageChange(value = R.id.content_main_view_pager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onMainPagerPageSelected(int position) {

        if (saveToHistory)
            pageHistory.push(MainViewPagerPositions.getValueOf(currentPos));

        currentPos = position;

        switch (MainViewPagerPositions.getValueOf(position)) {
            case BOT:
                dooitIcon.setVisibility(View.VISIBLE);
                setTitle("");
                break;
            case TARGET:
                dooitIcon.setVisibility(View.GONE);
                setTitle(GOALS);
                break;
            case CHALLENGE:
                dooitIcon.setVisibility(View.GONE);
                setTitle(CHALLENGE);
                break;
            case TIPS:
                dooitIcon.setVisibility(View.GONE);
                setTitle(TIPS);
                break;
            default:
                dooitIcon.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View tabView = tab.getCustomView();

            if (position == i) {
                MainViewPagerPositions.setActiveState(tabView);
            } else {
                MainViewPagerPositions.setInActiveState(tabView);
            }
        }

        // Notify analytics of Main view navigation
        onTrack();
    }

    @Override
    public void onBackPressed() {
        if (!pageHistory.isEmpty()) {
            saveToHistory = false;
            viewPager.setCurrentItem((pageHistory.pop()).getValue(), true);
            saveToHistory = true;
        } else
            super.onBackPressed(); // This will pop the Activity from the stack.
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
        User user = persisted.getCurrentUser();
        if (user == null)
            Snackbar.make(viewPager, R.string.prompt_relogin, Snackbar.LENGTH_LONG);
        else if (user.hasProfileImage())
            DraweeHelper.setProgressiveUri(
                    simpleDraweeViewProfile,
                    Uri.parse(user.getProfile().getProfileImageUrl())
            );
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
    public void setTitle(String newTitle){
        titleView.setText(newTitle);
    }

    @Override
    public String getScreenName() {
        return super.getScreenName() + " " + MainViewPagerPositions.getValueOf(viewPager.getCurrentItem()).name();
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

    /**
     * Gets extras from the notification to choose which Bot conversation to start for the Survey,
     * and the ID of the Survey.
     */
    private void handleSurveyNotification() {
        Bundle extras = getIntent().getExtras();
        if (extras == null)
            return;

        if (!extras.containsKey(NotificationArgs.SURVEY_ID)
                || !extras.containsKey(NotificationArgs.SURVEY_TYPE)) {
            Log.w(TAG, "Activity opened from Survey Notification without ID or BotType set up");
            return;
        }

        try {
            long id = Long.parseLong(extras.getString(NotificationArgs.SURVEY_ID));
            BotType type = BotType.valueOf(extras.getString(NotificationArgs.SURVEY_TYPE));

            // Persist for Bot requirements
            persisted.saveConvoSurveyId(type, id);
            startBot(type);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed parsing survey id", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to parse bot type", e);
        }
    }

    private void startBot(BotType type) {
        BotFragment fragment = (BotFragment) viewPager.getAdapter().instantiateItem(viewPager, MainViewPagerPositions.BOT.getValue());
        fragment.setBotType(type);
        fragment.setClearState(true);
        startPage(MainViewPagerPositions.BOT);
    }
}
