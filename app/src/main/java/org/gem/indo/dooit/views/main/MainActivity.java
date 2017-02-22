package org.gem.indo.dooit.views.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jinatonic.confetti.CommonConfetti;
import com.google.android.gms.analytics.Tracker;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.activity.result.ActivityForResultHelper;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.helpers.notifications.NotificationType;
import org.gem.indo.dooit.helpers.prefetching.PrefetchAlarmReceiver;
import org.gem.indo.dooit.models.User;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.services.NotificationAlarm;
import org.gem.indo.dooit.services.NotificationArgs;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.adapters.MainTabAdapter;
import org.gem.indo.dooit.views.main.fragments.ChallengeLightboxFragment;
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
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends DooitActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE = 0;

    private Subscription challengeSubscription;


    @BindView(R.id.activity_main)
    ViewGroup container;

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

    @Inject
    ChallengeManager challengeManager;

    private Stack<MainViewPagerPositions> pageHistory;
    private int currentPos;
    // Guard flag to prevent pushing to history when pressing back
    private boolean saveToHistory;

    static boolean prefetchAlarmHasBeenSet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);

        challengeSubscription = challengeManager.retrieveCurrentChallenge(true, new DooitErrorHandler() {
            @Override
            public void onError(final DooitAPIError error) {
                //The challenge could not be retried so do nothing
            }
        }).subscribe(new Action1<BaseChallenge>() {
            @Override
            public void call(BaseChallenge challenge) {
                if(challenge != null){
                    if(challenge.isActive()){
                        ChallengeLightboxFragment challengeLightboxFragment = ChallengeLightboxFragment.newInstance(challenge);
                        challengeLightboxFragment.show(getFragmentManager(), "challenge_available_lightbox");
                    }
                }
            }
        });

        if(!prefetchAlarmHasBeenSet) {
            //set alarm for pre fetching
            Intent intent = new Intent(MainActivity.this, PrefetchAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, REQUEST_CODE, intent, 0);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            prefetchAlarmHasBeenSet = true;
        }

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
                case SURVEY_REMINDER_1:
                case SURVEY_REMINDER_2:
                    handleSurveyNotification();
                    break;
                case CHALLENGE_WINNER:
                    if (persisted.hasConvoWinner(BotType.CHALLENGE_WINNER))
                        startBot(BotType.CHALLENGE_WINNER);
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
        mainTabAdapter.setAdapterListener(new MainTabAdapter.MainTabAdapterListener() {
            @Override
            public void onAdapterInstantiated() {
                onMainPagerPageSelected(currentPos);
            }
        });
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

        MainFragment fragment = (MainFragment) mainTabAdapter.getItem(viewPager.getCurrentItem());
        if (fragment != null)
            fragment.onActive();

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
        if (data != null) {
            switch (requestCode) {
                case RequestCodes.CHALLENGE_PARTICIPANT_BADGE:
                    if (persisted.hasConvoParticipant(BotType.CHALLENGE_PARTICIPANT_BADGE))
                        startBot(BotType.CHALLENGE_PARTICIPANT_BADGE);
            }
        }
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

    public void setTitle(String newTitle) {
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

    public void showConfetti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            // The confetti is scheduled to be drawn when the layout is complete. If it is called
            // immediately in onCreate or onResume, the confetti will be drawn to a canvas that is
            // painted over later by the MainActivity view, hiding the confetti.
            container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    CommonConfetti.rainingConfetti(container, new int[]{Color.RED, Color.BLUE}).oneShot();
                }
            });
    }
}
