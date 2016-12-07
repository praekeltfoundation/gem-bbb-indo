package org.gem.indo.dooit.views.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChangeSecurityQuestionActivity extends AppCompatActivity {
    /*************
     * Variables *
     *************/

    Unbinder unbinder = null;


    /************
     * Bindings *
     ************/

    @BindView(R.id.activity_onboarding_change_secq_background)
    NestedScrollView background;


    /***********************
     * Lifecycle overrides *
     ***********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_change_security_question);
        ((DooitApplication) getApplication()).component.inject(this);
        unbinder = ButterKnife.bind(this);
        SquiggleBackgroundHelper.setBackground(this, R.color.purple, R.color.purple_light, background);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChangeSecurityQuestionActivity.Builder create(Context context) {
            ChangeSecurityQuestionActivity.Builder builder = new ChangeSecurityQuestionActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChangeSecurityQuestionActivity.class);
        }
    }

}
