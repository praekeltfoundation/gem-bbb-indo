package com.rr.rgem.gem;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebase.client.Firebase;
import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.CoachConversation;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

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
