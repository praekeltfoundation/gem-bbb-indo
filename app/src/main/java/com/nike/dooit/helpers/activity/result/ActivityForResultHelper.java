package com.nike.dooit.helpers.activity.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Bernhard Müller on 11/14/2016.
 */
@Singleton
public class ActivityForResultHelper {
    ArrayList<ActivityForResultRequest> requests = new ArrayList<>();

    @Inject
    public ActivityForResultHelper() {
    }

    public void startActivityForResult(Context context, Intent intent, ActivityForResultCallback callback) {
        startActivityForResult((Activity) context, intent, callback);
    }

    public void startActivityForResult(Activity activity, Intent intent, ActivityForResultCallback callback) {
        if (intent == null) {
            return;
        }
        if (activity == null) {
            return;
        }

        ActivityForResultRequest request = new ActivityForResultRequest(callback);
        requests.add(request);

        activity.startActivityForResult(intent, request.getRequestCode());
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityForResultRequest requestResult = new ActivityForResultRequest(requestCode);
        if (requests.contains(requestResult)) {
            ActivityForResultRequest request = requests.get(requests.indexOf(requestResult));
            if (resultCode == Activity.RESULT_OK) {
                request.getCallback().onActivityResultOK(data);
            } else {
                request.getCallback().onActivityResultCanceled(data);
            }
            requests.remove(requestResult);
            return true;
        }
        return false;
    }
}
