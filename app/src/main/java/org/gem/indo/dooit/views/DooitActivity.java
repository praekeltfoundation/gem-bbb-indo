package org.gem.indo.dooit.views;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.gem.indo.dooit.BuildConfig;
import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Connectivity.NetworkChangeReceiver;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;
import org.gem.indo.dooit.models.User;

import java.util.Locale;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by wsche on 2016/11/05.
 */

public abstract class DooitActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Inject
    protected PermissionsHelper permissionsHelper;

    @Inject
    Tracker tracker;

    @Inject
    Persisted persisted;

    ProgressDialog dialog;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);
        // TODO: Move this to where you establish a user session
        if (!BuildConfig.DEBUG)
            logUser();
    }

    @Override
    protected void onDestroy() {
        // Ensure dialog is dismissed before activity is cleared
        if (dialog != null)
            dialog.dismiss();
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver == null) {
            receiver = NetworkChangeReceiver.createNetworkBroadcastReceiver(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            intentFilter.addAction(NetworkChangeReceiver.BROADCAST_ID);
            registerReceiver(receiver, intentFilter);
        }
        if (!NetworkChangeReceiver.isOnline(getBaseContext())) {
            showProgressDialog(R.string.waiting_for_internet_connection);
            //setViewEnabled(this.findViewById(android.R.id.content), false);
        } else {
            dismissDialog();
        }

        onTrack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    protected void onTrack() {
        if (BuildConfig.DEBUG)
            return;

        if (tracker != null) {
            tracker.setScreenName(getScreenName());
            tracker.send(new HitBuilders.ScreenViewBuilder()
                    .setCustomDimension(1, persisted.getUserUUID())
                    .build());
        } else
            Log.w(this.getClass().getName(), "Analytics tracker not instantiated");
    }

    public String getScreenName() {
        return this.getClass().getSimpleName().replaceAll("Activity|Fragment", "");
    }

    public Locale getLocal() {
        return Locale.getDefault();
    }

    private void logUser() {
        User user = persisted.getCurrentUser();

        if (user == null)
            return;

        String email = "";
        if (user.hasEmail())
            email = user.getEmail();

        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(Long.toString(user.getId()));
        Crashlytics.setUserName(user.getUsername());
        Crashlytics.setUserEmail(email);

        // User ID and Session tracking in Google Analytics
        if (user.hasId())
            tracker.set("&uid", Long.toString(user.getId()));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void hideKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public ProgressDialog showProgressDialog(@StringRes int messageRes) {
        String message = getString(messageRes);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new ProgressDialog(DooitActivity.this);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        return dialog;
    }

    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    @CallSuper
    public void onConnectionLost() {
        NetworkChangeReceiver.notifyUserOfNoInternetConnection(getBaseContext());
        showProgressDialog(R.string.waiting_for_internet_connection);
        //setViewEnabled(this.findViewById(android.R.id.content), false);
    }

    @CallSuper
    public void onConnectionReestablished() {
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        //setViewEnabled(this.findViewById(android.R.id.content), true);
        dismissDialog();
    }

    protected static void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewEnabled(child, enabled);
            }
        }
    }
}
