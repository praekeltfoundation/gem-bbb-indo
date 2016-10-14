package com.rr.rgem.gem;

import android.os.Bundle;
import android.util.Log;

import com.firebase.client.Firebase;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;

/**
 * Created by sjj98 on 9/26/2016.
 */
//Hooks into our entire life cycle of our app
public class GEM extends android.app.Application implements WebServiceApplication {

    private WebServiceFactory webService;

    @Override
    public void onCreate() {
        super.onCreate();
        webService = new WebServiceFactory("http://10.0.2.2:8000");
        Log.d("Application", "Application onCreate");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

    @Override
    public WebServiceFactory getWebServiceFactory() {
        return webService;
    }
}
