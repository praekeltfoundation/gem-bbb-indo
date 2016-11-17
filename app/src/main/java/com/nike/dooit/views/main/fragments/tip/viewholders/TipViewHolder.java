package com.nike.dooit.views.main.fragments.tip.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.views.tip.TipArticleActivity;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = TipViewHolder.class.getName();

    @BindString(R.string.tips_article_opening)
    String openingArticleText;

    @BindString(R.string.tips_article_add_fav)
    String addFavArticleText;

    @BindView(R.id.card_tip_image)
    SimpleDraweeView imageView;

    @BindView(R.id.card_tip_tags_view)
    ViewGroup tagsView;

    @BindView(R.id.card_tip_title)
    TextView titleView;

    @BindView(R.id.card_tip_fav)
    ImageView favView;

    @BindView(R.id.card_tip_share)
    ImageView shareView;

    @Inject
    TipManager tipManager;

    @Inject
    Persisted persisted;

    private int id;
    private String title;
    private String articleUrl;
    private boolean isFavourite;

    public TipViewHolder(View itemView) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @OnClick(R.id.card_tip_title)
    public void startArticle(View view) {
        Toast.makeText(view.getContext(),
                String.format(openingArticleText, titleView.getText().toString()),
                Toast.LENGTH_SHORT).show();
        TipArticleActivity.Builder.create(getContext())
                .putArticleUrl(articleUrl)
                .startActivity();
    }

    @OnClick(R.id.card_tip_fav)
    public void favouriteTip(final View view) {
        Observable<EmptyResponse> ob;

        if (isFavourite)
            ob = tipManager.unfavourite(id, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            });
        else
            ob = tipManager.favourite(id, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            });

        ob.subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        setFavourite(true);
                        persisted.clearFavourites();
                        Toast.makeText(getContext(), String.format(addFavArticleText,
                                titleView.getText().toString()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @OnClick(R.id.card_tip_share)
    public void shareTip(View view) {

    }

    public void setImageUri(Uri uri) {
        imageView.setImageURI(uri);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(SpannableString title) {
        titleView.setText(title);
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void clearTags() {
        tagsView.removeAllViews();
    }

    public void addTags(List<String> tags) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String tag : tags) {
            // Adding child view later to avoid bug where text isn't set.
            ViewGroup v = (ViewGroup) inflater.inflate(R.layout.card_tip_tag, tagsView, false);
            TextView textView = ((TextView) v.findViewById(R.id.card_tip_tag_text_view));
            textView.setText(tag);
            tagsView.addView(v);
        }
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        // TODO: Proper checkable button
        if (isFavourite) {
            favView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_heart));
        } else {
            favView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_heart_inverted));
        }
    }
}
