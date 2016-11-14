package com.nike.dooit.helpers.activity.result;

import android.content.Intent;

/**
 * Created by Bernhard Müller on 11/14/2016.
 */

public abstract class ActivityForResultCallback {
    public abstract void onActivityResultOK(Intent data);

    public void onActivityResultCanceled(Intent data) {
    }
}
