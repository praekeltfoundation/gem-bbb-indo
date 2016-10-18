package com.rr.rgem.gem.navigation;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rr.rgem.gem.ChallengeActivity;
import com.rr.rgem.gem.GoalActivity;
import com.rr.rgem.gem.GoalsActivity;
import com.rr.rgem.gem.LoginActivity;
import com.rr.rgem.gem.R;
import com.rr.rgem.gem.RegistrationActivity;
import com.rr.rgem.gem.RegistrationCompleteActivity;
import com.rr.rgem.gem.SavingsActivity;
import com.rr.rgem.gem.TipArchiveActivity;
import com.rr.rgem.gem.TipsActivity;
import com.rr.rgem.gem.models.Challenge;
import com.rr.rgem.gem.models.Tips;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by chris on 9/12/2016.
 */
public class GEMNavigation implements NavigationView.OnNavigationItemSelectedListener {
    private final AppCompatActivity container;
    Toolbar toolbar;
    public GEMNavigation(AppCompatActivity container){
        this.container = container;
        Toolbar toolbar = (Toolbar) container.findViewById(R.id.toolbar);
        if(toolbar==null){
            Utils.toast(container, "the toolbar could not be found in the supplied container argument");
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

        /*
        ImageView profilePicture = (ImageView) toolbar.findViewById(R.id.imageView);
        File profile = Utils.getFileFromName("profile.jpg", container.getBaseContext());
        if (Utils.fileExists(profile))
        {
            try {
                Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(profile));
                profilePicture.setImageBitmap(photo);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        */

    }
   public View addLayout(int id){
        RelativeLayout inner = (RelativeLayout)container.findViewById(R.id.content_main);
        if(inner==null){
            Utils.toast(container,"the main content view could not be found");
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
