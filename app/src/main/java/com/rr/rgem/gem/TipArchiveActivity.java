package com.rr.rgem.gem;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class TipArchiveActivity extends ApplicationActivity {

    static String TAB_FAVOURITES = "favourites";
    static String TAB_ALL = "all";

    private GEMNavigation navigation;
    private LinearLayout tipScreen;
    private TabHost tabHost;

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
                .setContent(new TipListScreen(this, new TipFactory() {
                    @Override
                    public List<Tip> initialTips() {
                        List<Tip> tips = new ArrayList();
                        tips.add(createTip("Fav Tip 1"));
                        tips.add(createTip("Fav Tip 2"));
                        tips.add(createTip("Fav Tip 3"));
                        tips.add(createTip("Fav Tip 4"));
                        return tips;
                    }
                }))
                .setIndicator(getResources().getString(R.string.favourites))
        );

        tabHost.addTab(tabHost.newTabSpec(TAB_ALL)
                .setContent(new TipListScreen(this, new TipFactory() {
                    @Override
                    public List<Tip> initialTips() {
                        List<Tip> tips = new ArrayList();
                        tips.add(createTip("All Tip 1"));
                        tips.add(createTip("All Tip 2"));
                        tips.add(createTip("All Tip 3"));
                        tips.add(createTip("All Tip 4"));
                        tips.add(createTip("All Tip 5"));
                        tips.add(createTip("All Tip 6"));
                        tips.add(createTip("All Tip 7"));
                        tips.add(createTip("All Tip 8"));
                        tips.add(createTip("All Tip 9"));
                        tips.add(createTip("All Tip 10"));
                        tips.add(createTip("All Tip 11"));
                        tips.add(createTip("All Tip 12"));
                        return tips;
                    }
                }))
                .setIndicator(getResources().getString(R.string.all))
        );
    }

    private Tip createTip(String name) {
        Tip tip = new Tip();
        tip.setName(name);
        tip.setCompleted("no");
        return tip;
    }

    interface TipFactory {
        List<Tip> initialTips();
    }

    class TipListScreen implements TabHost.TabContentFactory {

        private ApplicationActivity activity;
        private TipFactory factory;
        private ViewGroup tipContainer;
        private List<Tip> tips;
        private int cardCount;

        public TipListScreen(ApplicationActivity activity, TipFactory factory) {
            this.activity = activity;
            this.factory = factory;
        }

        @Override
        public View createTabContent(String tag) {
            LinearLayout view = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tip_list, null);
            tipContainer = (LinearLayout) view.findViewById(R.id.tipContainer);

            Log.d("TipArchive", "Creating content");

            tips = factory.initialTips();
            cardCount = 0;

            for (Tip tip : tips) {
                addTipCard(tip);
            }

            return view;
        }

        private void addTipCard(final Tip tip) {
            View view = LayoutInflater.from(tipContainer.getContext()).inflate(R.layout.tip_card, null);
            // Params required for cards to resize on add
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

            // Every second row contains two cards
            int nextCount = cardCount + 1;
            if (nextCount % 3 == 0) {
                // Insert view into last existing row
                ViewGroup row = (ViewGroup) tipContainer.getChildAt(tipContainer.getChildCount() - 1);
                row.addView(view, params);
            } else {
                // Create new row
                ViewGroup row = newRow();
                row.addView(view, params);
            }

            TextView title = (TextView) view.findViewById(R.id.tipTitle);
            RelativeLayout tipCardHead = (RelativeLayout) view.findViewById(R.id.tipCardHead);
            ImageView tipCardImage = (ImageView) view.findViewById(R.id.tipCardImage);
            ImageView favBtn = (ImageView) view.findViewById(R.id.favBtn);
            ImageView shareBtn = (ImageView) view.findViewById(R.id.shareBtn);

            title.setText(tip.getName());

            // Bind events
            final Context context = activity;

            tipCardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.toast(context, String.format("'%s' ARTICLE clicked...", tip.getName()));
                }
            });

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

            cardCount++;
        }

        private ViewGroup newRow() {
            LinearLayout row = new LinearLayout(tipContainer.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setTag("row");
            tipContainer.addView(row);
            return row;
        }
    }
}
