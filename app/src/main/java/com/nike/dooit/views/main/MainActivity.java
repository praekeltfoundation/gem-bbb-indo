package com.nike.dooit.views.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nike.dooit.R;
import com.nike.dooit.views.helpers.activity.DooitActivityBuilder;
import com.nike.dooit.views.main.adapters.MainTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content_main_view_pager)
    ViewPager viewPager;

    @BindView(R.id.content_main_tab_layout)
    TabLayout tabLayout;

    MainTabAdapter mainTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.sym_def_app_icon);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProfile();
                }
            });
        }
        mainTabAdapter = new MainTabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mainTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mainTabAdapter.getTabView(i));
        }
    }

    private void openProfile() {

    }

    public static class Builder extends DooitActivityBuilder<MainActivity.Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static MainActivity.Builder create(Context context) {
            MainActivity.Builder builder = new MainActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, MainActivity.class);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
