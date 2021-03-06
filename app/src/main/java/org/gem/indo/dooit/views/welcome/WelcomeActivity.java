package org.gem.indo.dooit.views.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.onboarding.RegistrationActivity;
import org.gem.indo.dooit.views.welcome.adapter.WelcomeTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends DooitActivity {

    @BindView(R.id.activity_welcome_view_pager)
    ViewPager viewPager;

    @BindView(R.id.activity_welcome_indicator)
    CirclePageIndicator circleIndicator;

    @BindView(R.id.activity_welcome_button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        viewPager.setAdapter(new WelcomeTabAdapter(getSupportFragmentManager(), this));
        viewPager.setOffscreenPageLimit(4);
        circleIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (WelcomeViewPagerPositions.getValueOf(position) == WelcomeViewPagerPositions.THREE) {
                    button.setVisibility(View.VISIBLE);
                    circleIndicator.setVisibility(View.GONE);
                } else {
                    button.setVisibility(View.GONE);
                    circleIndicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick(R.id.activity_welcome_button)
    public void start() {
        RegistrationActivity.Builder.create(this).startActivityClearTop();
    }


    public static class Builder extends DooitActivityBuilder<Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static WelcomeActivity.Builder create(Context context) {
            WelcomeActivity.Builder builder = new WelcomeActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, WelcomeActivity.class);
        }

    }
}
