package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.List;

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
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebServiceFactory factory = ((WebServiceApplication) getApplication()).getWebServiceFactory();
        service = factory.createService(CMSService.class);

        Utils.toast(this, "starting Tips Archive activity");
        navigation = new GEMNavigation(this);
        tipScreen = (LinearLayout) navigation.addLayout(R.layout.tip_archive);

        // Initialize Tabs
        tabHost = (TabHost) tipScreen.findViewById(R.id.tipTabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec(TAB_FAVOURITES)
                .setContent(new TipListScreen(this, new TipFactory() {
                    @Override
                    public void loadTips(TipCallback callback) {
                        // TODO: Tip Favourites
                        callback.onEmpty();
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

        private ApplicationActivity activity;
        private TipFactory factory;
        private ViewGroup tipContainer;
        private View textViewEmpty;
        private View textViewLoading;
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
            imageDownloader = new ImageDownloader(client);
            imageStorage = new ImageStorage(activity, TIP_IMAGE_DIR);
        }

        @Override
        public View createTabContent(String tag) {
            ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.tip_list, null);
            textViewEmpty = view.findViewById(R.id.textViewEmpty);
            textViewLoading = view.findViewById(R.id.textViewLoading);
            tipContainer = (ViewGroup) view.findViewById(R.id.tipContainer);

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
            final ImageView tipCardImage = (ImageView) view.findViewById(R.id.tipCardImage);
            ImageView favBtn = (ImageView) view.findViewById(R.id.favBtn);
            ImageView shareBtn = (ImageView) view.findViewById(R.id.shareBtn);

            title.setText(tip.getTitle());
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
