package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import com.rr.rgem.gem.image.ImageCallback;
import com.rr.rgem.gem.image.ImageDownloader;
import com.rr.rgem.gem.image.ImageStorage;
import com.rr.rgem.gem.models.Tip;
import com.rr.rgem.gem.models.TipArticle;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.service.CMSService;
import com.rr.rgem.gem.service.WebServiceApplication;
import com.rr.rgem.gem.service.WebServiceFactory;
import com.rr.rgem.gem.views.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Wimpie Victor on 2016/10/05.
 */
public class TipArchiveActivity extends ApplicationActivity {

    private final static String TAG = "TipArchive";
    private final static String TAB_FAVOURITES = "favourites";
    private final static String TAB_ALL = "all";

    private GEMNavigation navigation;
    private CMSService service;
    private LinearLayout tipScreen;
    private SharedPreferences sharedPreferences;
    //private TabHost tabHost;

    @BindView(R.id.tipTabHost) TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        service = factory.createService(CMSService.class);

        Utils.toast(this, "starting Tips Archive activity");
        navigation = new GEMNavigation(this);
        tipScreen = (LinearLayout) navigation.addLayout(R.layout.tip_archive);

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        ButterKnife.bind(this);

        SearchView searchBar = (SearchView) tipScreen.findViewById(R.id.tipSearchBar);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

             @Override
             public boolean onQueryTextSubmit(String query) {
                 LinearLayout container = (LinearLayout) tipScreen.findViewById(R.id.tipContainer);

                 for (int i = 0; i < container.getChildCount(); ++i) {
                     TextView text = (TextView) container.getChildAt(i).findViewById(R.id.tipTitle);
                     if (!text.getText().toString().contains(query))
                        container.getChildAt(i).setVisibility(View.GONE);
                     else
                         container.getChildAt(i).setVisibility(View.VISIBLE);
                 }

                 return false;
             }

             @Override
             public boolean onQueryTextChange(String newText) {
                 return false;
             }

        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                LinearLayout container = (LinearLayout) tipScreen.findViewById(R.id.tipContainer);

                for (int i = 0; i < container.getChildCount(); ++i) {
                    TextView text = (TextView) container.getChildAt(i).findViewById(R.id.tipTitle);
                    container.getChildAt(i).setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        // Initialize Tabs
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec(TAB_FAVOURITES)
                .setContent(new TipListScreen(this, new TipFactory() {
                    @Override
                    public void loadTips(final TipCallback callback) {
                        Log.d(TAG, "TipCallback loading tips");
                        TipArchiveActivity.this.service.listTips().enqueue(new Callback<List<TipArticle>>() {
                            @Override
                            public void onResponse(Call<List<TipArticle>> call, Response<List<TipArticle>> response) {
                                Log.d(TAG, "Loading tips status " + response.code());
                                if (response.isSuccessful()) {
                                    List<TipArticle> tips = response.body();
                                    List<TipArticle> favourites = new ArrayList<TipArticle>();

                                    for (TipArticle tip : tips) {
                                        if(sharedPreferences.getBoolean("Tip" + tip.getId() + "_isFavourite", false)) {
                                            favourites.add(tip);
                                        }
                                    }

                                    if (favourites.isEmpty()) {
                                        callback.onEmpty();
                                    } else {
                                        callback.onLoad(favourites);
                                    }
                                } else {
                                    // TODO: Handle error
                                    try {
                                        Log.d(TAG, "Tips not loaded " + response.errorBody().string());
                                    } catch (IOException e) {
                                        Log.d(TAG, "IO Exception while reading tips error body", e);
                                    }
                                    callback.onEmpty();
                                }

                            }
                            @Override
                            public void onFailure(Call<List<TipArticle>> call, Throwable t) {
                                // TODO: Handle error
                                Log.d(TAG, "Loading tips exception", t);
                                callback.onEmpty();
                            }
                        });
                    }
                }))
                .setIndicator(getResources().getString(R.string.favourites))
        );

        tabHost.addTab(tabHost.newTabSpec(TAB_ALL)
                .setContent(new TipListScreen(this, new TipFactory() {

                    @Override
                    public void loadTips(final TipCallback callback) {
                        Log.d(TAG, "TipCallback loading tips");
                        TipArchiveActivity.this.service.listTips().enqueue(new Callback<List<TipArticle>>() {
                            @Override
                            public void onResponse(Call<List<TipArticle>> call, Response<List<TipArticle>> response) {
                                Log.d(TAG, "Loading tips status " + response.code());
                                if (response.isSuccessful()) {
                                    List<TipArticle> tips = response.body();
                                    if (tips.isEmpty()) {
                                        callback.onEmpty();
                                    } else {
                                        callback.onLoad(tips);
                                    }
                                } else {
                                    // TODO: Handle error
                                    try {
                                        Log.d(TAG, "Tips not loaded " + response.errorBody().string());
                                    } catch (IOException e) {
                                        Log.d(TAG, "IO Exception while reading tips error body", e);
                                    }
                                    callback.onEmpty();
                                }

                            }

                            @Override
                            public void onFailure(Call<List<TipArticle>> call, Throwable t) {
                                // TODO: Handle error
                                Log.d(TAG, "Loading tips exception", t);
                                callback.onEmpty();
                            }
                        });
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
        void loadTips(final TipCallback callback);
    }

    interface TipCallback {
        void onLoad(List<TipArticle> tips);
        void onEmpty();
    }

    class TipListScreen implements TabHost.TabContentFactory {

        private static final String TAG = "TipList";
        private static final String TIP_IMAGE_DIR = "tip-images";

        @BindView(R.id.tipContainer) ViewGroup tipContainer;
        @BindView(R.id.textViewEmpty) View textViewEmpty;
        @BindView(R.id.textViewLoading) View textViewLoading;

        private ApplicationActivity activity;
        private TipFactory factory;
        private List<TipArticle> tips;
        private WebServiceFactory webFactory;
        private ImageDownloader imageDownloader;
        private ImageStorage imageStorage;
        private int cardCount;

        public TipListScreen(ApplicationActivity activity, TipFactory factory) {
            this.activity = activity;
            this.factory = factory;

            webFactory = ((WebServiceApplication) activity.getApplication()).getWebServiceFactory();
            OkHttpClient client = webFactory.getClient();
            imageDownloader = webFactory.createImageDownloader();
            imageStorage = new ImageStorage(activity, TIP_IMAGE_DIR);
        }

        @Override
        public View createTabContent(String tag) {
            ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.tip_list, null);
            ButterKnife.bind(this, view);

            Log.d(TAG, "Creating tab content");
            cardCount = 0;

            textViewEmpty.setVisibility(View.GONE);
            textViewLoading.setVisibility(View.VISIBLE);
            factory.loadTips(new TipCallback() {
                @Override
                public void onLoad(List<TipArticle> tips) {
                    TipListScreen.this.tips = tips;
                    textViewLoading.setVisibility(View.GONE);
                    for (TipArticle tip : tips) {
                        TipListScreen.this.addTipCard(tip);
                    }
                }

                @Override
                public void onEmpty() {
                    Log.d(TAG, "Tips Empty");
                    textViewLoading.setVisibility(View.GONE);
                    textViewEmpty.setVisibility(View.VISIBLE);
                }
            });

            return view;
        }

        private void addTipCard(final TipArticle tip) {
            final View view = LayoutInflater.from(tipContainer.getContext()).inflate(R.layout.tip_card, null);
            view.setTag(Integer.toString(tip.getId()));
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
            final ImageView tipCardImage = (ImageView) view.findViewById(R.id.tipCardImage);
            final ImageView favBtn = (ImageView) view.findViewById(R.id.favBtn);
            ImageView shareBtn = (ImageView) view.findViewById(R.id.shareBtn);

            title.setText(tip.getTitle());
            tip.setFavourite(sharedPreferences.getBoolean("Tip" + tip.getId() + "_isFavourite", false));
            setFavButtonColor(favBtn, tip.isFavourite());
            try {
                if (tip.hasCoverImageUrl()) {
                    // Attempt to get image from internal storage
                    Bitmap image = imageStorage.loadImage(tip.getCoverImageUrl());
                    if (image != null) {
                        tipCardImage.setImageBitmap(image);
                        Log.d(TAG, String.format("Image loaded locally [%s]", tip.getCoverImageUrl()));
                    } else {
                        imageDownloader.retrieveImage(webFactory.joinUrl(tip.getCoverImageUrl()), new ImageCallback() {
                            @Override
                            public void onLoad(Bitmap image) {
                                tipCardImage.setImageBitmap(image);
                                imageStorage.saveImage(tip.getCoverImageUrl(), image);
                            }

                            @Override
                            public void onFailure() {
                                Log.d(TAG, "Failed to set tip card image");
                            }
                        });
                    }
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Image download URL join fail", e);
            }

            // Bind events
            final TipListScreen screen = this;
            final Context context = activity;

            tipCardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.toast(context, String.format("'%s' ARTICLE clicked...", tip.getTitle()));
                    startArticleActivity(tip.getArticleUrl());
                }
            });

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleTipFavourite(TipListScreen.this, tip, Integer.toString(tip.getId()));
                    Utils.toast(context, String.format("'%s' FAVOURITE clicked...", tip.getTitle()));
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Utils.toast(context, String.format("'%s' SHARE clicked...", tip.getTitle()));
                }
            });

            cardCount++;
        }

        private void setFavButtonColor(ImageView favBtn, boolean favourite)
        {
            if(favourite)
                DrawableCompat.setTint(favBtn.getDrawable(), Color.YELLOW);
            else
                DrawableCompat.setTint(favBtn.getDrawable(), Color.BLACK);
        }

        private void toggleTipFavourite(TipListScreen list, TipArticle tip, String tag) {

            int tracker = tabHost.getCurrentTab();

            tabHost.setCurrentTab(2);
            View cardAll = tabHost.getChildAt(0).findViewWithTag(tag);
            tabHost.setCurrentTab(1);
            View cardFav = tabHost.getChildAt(0).findViewWithTag(tag);

            if(tip.isFavourite()) {
                tip.setFavourite(false);
                sharedPreferences.edit().putBoolean("Tip" + tip.getId() + "_isFavourite", false).apply();
                setFavButtonColor((ImageView) cardAll.findViewById(R.id.favBtn), false);

                if(cardFav != null)
                    setFavButtonColor((ImageView) cardFav.findViewById(R.id.favBtn), false);
            }
            else {
                tip.setFavourite(true);
                sharedPreferences.edit().putBoolean("Tip" + tip.getId() + "_isFavourite", true).apply();
                setFavButtonColor((ImageView) cardAll.findViewById(R.id.favBtn), true);

                if(cardFav == null)
                    list.addTipCard(tip);
                else
                    setFavButtonColor((ImageView) cardFav.findViewById(R.id.favBtn), true);
            }

            tabHost.setCurrentTab(tracker);
        }

        private ViewGroup newRow() {
            LinearLayout row = new LinearLayout(tipContainer.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setTag("row");
            tipContainer.addView(row);
            return row;
        }

        private void startArticleActivity(String url) {
            Intent intent = new Intent(activity, TipArticleActivity.class);
            intent.putExtra("url", url);
            activity.startActivity(intent);
        }
    }
}
