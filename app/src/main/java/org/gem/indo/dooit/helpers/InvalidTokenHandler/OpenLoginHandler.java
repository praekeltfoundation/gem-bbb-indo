package org.gem.indo.dooit.helpers.InvalidTokenHandler;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import org.gem.indo.dooit.BuildConfig;
import org.gem.indo.dooit.views.onboarding.LoginActivity;

import java.util.List;

/**
 * Created by Reinhardt on 2017/01/05.
 */

public class OpenLoginHandler implements InvalidTokenHandler {


    @Override
    public void handle(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses){
            if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(BuildConfig.APPLICATION_ID)){
                LoginActivity.Builder.create(context).startActivityClearTop();
                break;
            }
        }
    }
}
