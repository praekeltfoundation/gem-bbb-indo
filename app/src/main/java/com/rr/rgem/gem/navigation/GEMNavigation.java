package com.rr.rgem.gem.navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.GoalsActivity;
import com.rr.rgem.gem.LoginActivity;
import com.rr.rgem.gem.Persisted;
import com.rr.rgem.gem.R;
import com.rr.rgem.gem.RegistrationActivity;
import com.rr.rgem.gem.RegistrationCompleteActivity;
import com.rr.rgem.gem.SavingsActivity;
import com.rr.rgem.gem.SettingsActivity;
import com.rr.rgem.gem.TipArchiveActivity;
import com.rr.rgem.gem.TipsActivity;
import com.rr.rgem.gem.image.ImageHelper;
import com.rr.rgem.gem.image.ImageStorage;
import com.rr.rgem.gem.service.model.User;
import com.rr.rgem.gem.views.Utils;

/**
 * Created by chris on 9/12/2016.
 */
public class GEMNavigation implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "GEMNavigation";

    private final AppCompatActivity container;
    Toolbar toolbar;

    public GEMNavigation(AppCompatActivity container){
        this.container = container;
        Toolbar toolbar = (Toolbar) container.findViewById(R.id.toolbar);
        if(toolbar==null){
            Utils.toast(container, container.getResources().getString(R.string.toolBarNotSupplied));
            return;
        }
        container.setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) container.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                container, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) container.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationView navigationView_two = (NavigationView) container.findViewById(R.id.nav_view_right);
        navigationView_two.setNavigationItemSelectedListener(this);
        container.getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Special header method required, because NavigationView is a RecyclerView
        View headerView = navigationView.getHeaderView(0);
        Persisted persisted = new Persisted(container);
        User user = persisted.loadUser();
        Log.d(TAG, "Loaded user " + user);
        if (user.getLabel() != null) {
            Log.d(TAG, "Setting label");
            TextView nameTextView = (TextView) headerView.findViewById(R.id.textViewName);
            nameTextView.setText(user.getLabel());
        }

        Log.d(TAG, "Loading profile picture from storage");
        ImageView profileImageView = (ImageView) headerView.findViewById(R.id.imageProfile);
        ImageStorage imageStorage = new ImageStorage(container.getApplicationContext(),
                ImageHelper.IMAGE_DIRECTORY);
        Bitmap profileImage = imageStorage.loadImage(ImageHelper.PROFILE_IMAGE_FILENAME);
        if (profileImage != null) {
            profileImageView.setImageBitmap(profileImage);
        }
    }

    public View addLayout(int id){
        RelativeLayout inner = (RelativeLayout)container.findViewById(R.id.content_main);
        if(inner==null){
            Utils.toast(container,container.getResources().getString(R.string.contentNotSupplied));
            return null;
        }
        LayoutInflater factory = LayoutInflater.from(container);
        View layout = factory.inflate(id, null);
        inner.addView(layout);
        return  layout;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(container, RegistrationCompleteActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_registration) {
            Intent intent = new Intent(container, RegistrationActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_savings) {
            Intent intent = new Intent(container, SavingsActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_goals) {
            Intent intent = new Intent(container, GoalsActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(container, SettingsActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_tiparchive) {
            Intent intent = new Intent(container, TipArchiveActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_login) {
            Intent intent = new Intent(container, LoginActivity.class);
            container.startActivity(intent);
        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_terms_and_conditions) {

        } else if (id == R.id.nav_help) {

        }else if (id == R.id.nav_challenges) { //Right Drawer
            Intent intent = new Intent(container, ChallengeActivity.class);
            container.startActivity(intent);
        }
        else if (id == R.id.nav_tips) { //Right Drawer
            Intent intent = new Intent(container,TipsActivity.class);
            container.startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) this.container.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
