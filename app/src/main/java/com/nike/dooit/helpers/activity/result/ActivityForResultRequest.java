package com.nike.dooit.helpers.activity.result;

import java.util.Random;

/**
 * Created by Bernhard Müller on 11/14/2016.
 */

public class ActivityForResultRequest {
    private static Random random;
    private int requestCode;
    private ActivityForResultCallback callback;

    public ActivityForResultRequest(int requestCode) {
        this.requestCode = requestCode;
    }

    public ActivityForResultRequest(ActivityForResultCallback callback) {
        this.callback = callback;
        if (random == null) {
            random = new Random();
        }
        this.requestCode = random.nextInt(255);
    }

    public int getRequestCode() {
        return requestCode;
    }

    public ActivityForResultCallback getCallback() {
        return callback;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof ActivityForResultRequest) {
            return ((ActivityForResultRequest) object).requestCode == this.requestCode;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return requestCode;
    }
}
