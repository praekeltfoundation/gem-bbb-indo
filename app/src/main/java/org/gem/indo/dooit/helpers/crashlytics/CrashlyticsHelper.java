package org.gem.indo.dooit.helpers.crashlytics;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.gem.indo.dooit.BuildConfig;

/**
 * Created by frede on 2017/01/23.
 */

public class CrashlyticsHelper{

    public static void log(String TAG, String methodName, String message) {
        if (!BuildConfig.DEBUG)
            Crashlytics.log(Log.DEBUG, TAG + '.' + methodName + ":", message);
    }

    public static void logException(Throwable e){
        if(!BuildConfig.DEBUG)
            Crashlytics.logException(e);
    }
}
