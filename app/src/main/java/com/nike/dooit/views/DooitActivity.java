package com.nike.dooit.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.nike.dooit.Constants;
import com.nike.dooit.helpers.permissions.PermissionsHelper;

import java.util.Locale;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by wsche on 2016/11/05.
 */

public class DooitActivity extends AppCompatActivity {

    @Inject
    protected PermissionsHelper permissionsHelper;
    ProgressDialog dialog;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Move this to where you establish a user session
        if (!Constants.DEBUG)
            logUser();
    }

    public Uri getRealPathFromURI(Uri contentUri) {
        try {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri;
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return Uri.parse(cursor.getString(index));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return contentUri;
    }

    public Locale getLocal() {
        return Locale.getDefault();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
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
        dialog = new ProgressDialog(this);
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
