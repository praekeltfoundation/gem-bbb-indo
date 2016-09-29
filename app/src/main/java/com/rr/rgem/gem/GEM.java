package com.rr.rgem.gem;

import android.os.Bundle;

import com.firebase.client.Firebase;

/**
 * Created by sjj98 on 9/26/2016.
 */
//Hooks into our entire life cycle of our app
public class GEM extends android.app.Application  {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
