package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.UserManager;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.custom.WigglyEditText;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.onboarding.fragments.PasswordResetUsernameFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Rudolph Jacobs on 2016-12-08.
 */

public class PasswordResetActivity extends DooitActivity {
    /*************
     * Constants *
     *************/

    public static final int minFieldLength = 6;


    /*************
     * Variables *
     *************/

    Unbinder unbinder = null;
    Subscription dataSubscription = null;


    /************
     * Bindings *
     ************/

    @Inject
    UserManager userManager;

    @BindView(R.id.activity_container)
    FrameLayout background;


    /****************
     * Constructors *
     ****************/

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static PasswordResetActivity.Builder create(Context context) {
            PasswordResetActivity.Builder builder = new PasswordResetActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, PasswordResetActivity.class);
        }
    }


    /***********************
     * Lifecycle overrides *
     ***********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        ((DooitApplication) getApplication()).component.inject(this);
        unbinder = ButterKnife.bind(this);
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = PasswordResetUsernameFragment.newInstance();
        ft.replace(R.id.activity_container, f, "password_reset_fragment");
        ft.commit();
    }

    @Override
    protected void onStop() {
        if (dataSubscription != null) {
            dataSubscription.unsubscribe();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
