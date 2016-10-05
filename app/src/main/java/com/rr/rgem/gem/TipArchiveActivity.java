package com.rr.rgem.gem;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.rr.rgem.gem.models.Tip;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.Utils;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/10/05.
 */
public class TipArchiveActivity extends ApplicationActivity {

    static String TAB_FAVOURITES = "Favourites";
    static String TAB_ALL = "All";

    private GEMNavigation navigation;
    private LinearLayout tipScreen;
    private TabHost tabHost;
    private List<Tip> tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.toast(this, "starting Tips Archive activity");
        navigation = new GEMNavigation(this);
        tipScreen = (LinearLayout) navigation.addLayout(R.layout.tip_archive);

        // Initialize Tabs
        tabHost = (TabHost) tipScreen.findViewById(R.id.tipTabHost);
        tabHost.setup();
        TabHost.TabSpec spec;

        spec = tabHost.newTabSpec(TAB_FAVOURITES);
        spec.setContent(R.id.favouritesTab);
        spec.setIndicator(TAB_FAVOURITES);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_ALL);
        spec.setContent(R.id.allTab);
        spec.setIndicator(TAB_ALL);
        tabHost.addTab(spec);
    }

}
