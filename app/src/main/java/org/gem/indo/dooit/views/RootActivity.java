package org.gem.indo.dooit.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.onboarding.LoginActivity;
import org.gem.indo.dooit.views.welcome.WelcomeActivity;

import javax.inject.Inject;

public class RootActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Inject
    Persisted persisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getApplication()).component.inject(this);

        //If the user was logged in
        if (persisted.hasToken()) {
            MainActivity.Builder.create(this).startActivityClearTop();
        } // If the user logged out
        else if (!persisted.isNewBotUser()){
            LoginActivity.Builder.create(this).startActivityClearTop();
        } //User was never active in the app before
        else{
            WelcomeActivity.Builder.create(this).startActivityClearTop();
        }
    }
}
