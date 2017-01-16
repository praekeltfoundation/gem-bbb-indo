package org.gem.indo.dooit.views.main.fragments.tip.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.api.responses.EmptyResponse;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.views.web.MinimalWebViewActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = TipViewHolder.class.getName();
    private static final String SCREEN_NAME_TIP_ARTICLE = "Tip Article";

    @BindString(R.string.tips_article_opening)
    String openingArticleText;

    @BindString(R.string.tips_article_add_fav)
    String addFavArticleText;

    @BindString(R.string.tips_article_remove_fav)
    String removeFavArticleText;

    @BindView(R.id.card_tip_image)
    SimpleDraweeView imageView;

    @BindView(R.id.card_tip_tags_view)
    HashtagView tagsView;

    @BindView(R.id.card_tip_title)
    TextView titleView;

    @BindView(R.id.card_tip_fav)
    ImageView favView;

    @BindView(R.id.card_tip_share)
    ImageView shareView;

    @BindView(R.id.card_tip_button_overlay)
    Button btnCover;

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
    public void clickOnTitle(View view) {
        startArticle(view);
    }

    @OnClick(R.id.card_tip_button_overlay)
    public void clickOnButton(View view) {
        startArticle(view);
    }

    public void startArticle(View view) {
        Toast.makeText(view.getContext(),
                String.format(openingArticleText, titleView.getText().toString()),
                Toast.LENGTH_SHORT).show();
        MinimalWebViewActivity.Builder.create(getContext())
                .setUrl(articleUrl)
                .setWebTipShare()
                .setTitle(titleView.getText().toString())
                .setWebTipId(id)
                .setScreenName(SCREEN_NAME_TIP_ARTICLE)
                .startActivity();
    }


    @OnClick(R.id.card_tip_fav)
    public void favouriteTip(final View view) {
        if (isFavourite) {
            tipManager.unfavourite(id, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {
                    Log.d(TAG, "Tip favourite status could not be set " + error.getMessage());
                }
            }).subscribe(new Action1<EmptyResponse>() {
                @Override
                public void call(EmptyResponse emptyResponse) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            setFavourite(false);
                            persisted.clearFavourites();
                            Toast.makeText(getContext(), String.format(removeFavArticleText,
                                    titleView.getText().toString()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            tipManager.favourite(id, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            }).subscribe(new Action1<EmptyResponse>() {
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
    }

    @OnClick(R.id.card_tip_share)
    public void shareTip(View view) {
        if (!TextUtils.isEmpty(articleUrl))
            new SocialSharer(getContext()).share(
                    getContext().getText(R.string.share_chooser_tip_title),
                    Uri.parse(articleUrl)
            );
    }

    public void setImageUri(Uri uri) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);
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

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        // TODO: Proper checkable button
        if (isFavourite)
            favView.setImageDrawable(ContextCompat.getDrawable(getContext(), org.gem.indo.dooit.R.drawable.ic_d_heart_yellow));
        else
            favView.setImageDrawable(ContextCompat.getDrawable(getContext(), org.gem.indo.dooit.R.drawable.ic_d_heart_yellow_inverted));
    }

    public void clearTags() {
        tagsView.removeAllViews();
    }

    public void addTags(List<String> tags) {
        tagsView.setData(tags, new HashtagView.DataTransform<String>() {
            @Override
            public CharSequence prepare(String item) {
                return item.toUpperCase();
            }
        });
    }
}
