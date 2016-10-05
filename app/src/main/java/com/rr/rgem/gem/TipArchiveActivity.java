package com.rr.rgem.gem;

import android.os.Bundle;

import com.rr.rgem.gem.models.Tip;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.Utils;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/10/05.
 */
public class TipArchiveActivity extends ApplicationActivity {

    private GEMNavigation navigation;
    private List<Tip> tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new GEMNavigation(this);
        Utils.toast(this, "starting Tips Archive activity");
    }

}
