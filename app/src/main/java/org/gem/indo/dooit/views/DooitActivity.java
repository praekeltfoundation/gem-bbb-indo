package org.gem.indo.dooit.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.gem.indo.dooit.BuildConfig;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.permissions.PermissionsHelper;
import org.gem.indo.dooit.models.User;

import java.util.Locale;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by wsche on 2016/11/05.
 */

public abstract class DooitActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        onTrack();
    }

    protected void onTrack() {
        if (BuildConfig.DEBUG)
            return;

        if (tracker != null) {
            tracker.setScreenName(getScreenName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } else
            Log.w(this.getClass().getName(), "Analytics tracker not instantiated");
    }

    protected String getScreenName() {
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
}
