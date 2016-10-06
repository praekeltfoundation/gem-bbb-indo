package com.rr.rgem.gem;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.rr.rgem.gem.models.Tip;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/10/05.
 */
public class TipArchiveActivity extends ApplicationActivity implements TabHost.TabContentFactory {

    static String TAB_FAVOURITES = "favourites";
    static String TAB_ALL = "all";

    private GEMNavigation navigation;
    private LinearLayout tipScreen;
    private LinearLayout tipContainer;
    private TabHost tabHost;
    private List<Tip> tips = new ArrayList<Tip>();

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

        tabHost.addTab(tabHost.newTabSpec(TAB_FAVOURITES)
                .setContent(this)
                .setIndicator(TAB_FAVOURITES)
        );

        tabHost.addTab(tabHost.newTabSpec(TAB_ALL)
                .setContent(this)
                .setIndicator(TAB_ALL)
        );
    }

    @Override
    public View createTabContent(String tag) {
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.tip_list, null);
        tipContainer = (LinearLayout) view.findViewById(R.id.tipContainer);

        tips.clear();

        if (tag.equals(TAB_FAVOURITES)) {
            tips.add(createTip("Fav Tip 1"));
            tips.add(createTip("Fav Tip 2"));
            tips.add(createTip("Fav Tip 3"));
        } else if (tag.equals(TAB_ALL)) {
            tips.add(createTip("All Tip 1"));
            tips.add(createTip("All Tip 2"));
            tips.add(createTip("All Tip 3"));
            tips.add(createTip("All Tip 4"));
        }

        for (Tip tip : tips) {
            addTipCard(tip);
        }

        return view;
    }

    private Tip createTip(String name) {
        Tip tip = new Tip();
        tip.setName(name);
        tip.setCompleted("no");
        return tip;
    }

    private void addTipCard(final Tip tip) {
        View view = LayoutInflater.from(tipContainer.getContext()).inflate(R.layout.tip_card, null);
        tipContainer.addView(view);

        TextView title = (TextView) view.findViewById(R.id.tipTitle);
        RelativeLayout tipCardHead = (RelativeLayout) view.findViewById(R.id.tipCardHead);
        ImageView favBtn = (ImageView) view.findViewById(R.id.favBtn);
        ImageView shareBtn = (ImageView) view.findViewById(R.id.shareBtn);

        title.setText(tip.getName());

        // Bind events
        final Context context = this;

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast(context, String.format("'%s' FAVOURITE clicked...", tip.getName()));
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utils.toast(context, String.format("'%s' SHARE clicked...", tip.getName()));
            }
        });
    }
}
