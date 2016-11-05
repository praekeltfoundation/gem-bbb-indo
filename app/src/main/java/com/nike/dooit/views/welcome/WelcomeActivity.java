package com.nike.dooit.views.welcome;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.nike.dooit.R;
import com.nike.dooit.views.onboarding.RegistrationActivity;
import com.nike.dooit.views.welcome.WelcomeTabAdapter.WelcomeTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.activity_welcome_view_pager)
    ViewPager viewPager;

    @BindView(R.id.activity_welcome_indicator)
    CircleIndicator circleIndicator;

    @BindView(R.id.activity_welcome_button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        viewPager.setAdapter(new WelcomeTabAdapter(getSupportFragmentManager(), this));
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
        RegistrationActivity.Builder.create(this).startActivity();
    }
}
