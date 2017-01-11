package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.web.MinimalWebViewActivity;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/11/29.
 */

public class TipBotViewHolder extends BaseBotViewHolder<Node> {

    @BindString(R.string.tips_article_opening)
    String openingText;

    @BindView(R.id.item_view_bot_tip)
    View background;

    @BindView(R.id.item_view_bot_tip_image)
    SimpleDraweeView imageView;

    @BindView(R.id.item_view_bot_tip_title)
    TextView titleView;

    @BindView(R.id.item_view_bot_tip_separator)
    View separator;

    @BindView(R.id.item_view_bot_tip_share)
    TextView shareView;

    @Inject
    Persisted persisted;

    private BotAdapter adapter;
    private HashtagView.TagsClickListener listener;

    public TipBotViewHolder(View itemView, BotAdapter adapter, HashtagView.TagsClickListener listener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
        this.listener = listener;

        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
        separator.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        final int id = dataModel.values.getInteger(BotParamType.TIP_ID.getKey());
        final String title = dataModel.values.getString(BotParamType.TIP_TITLE.getKey());
        final String imageUrl = dataModel.values.getString(BotParamType.TIP_IMAGE_URL.getKey());
        final String articleUrl = dataModel.values.getString(BotParamType.TIP_ARTICLE_URL.getKey());

        if (!TextUtils.isEmpty(imageUrl))
            DraweeHelper.setProgressiveUri(imageView, Uri.parse(imageUrl));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer answer = new Answer();
                answer.setName(dataModel.getName());
                answer.setType(BotMessageType.BLANKANSWER);
                answer.setValue(imageUrl);
                answer.setText(null);
                listener.onItemClicked(answer);

                MainActivity activity = (MainActivity) getContext();
                if (activity != null)
                    activity.startPage(MainViewPagerPositions.TIPS);

                Toast.makeText(getContext(), String.format(openingText, title), Toast.LENGTH_LONG).show();
                MinimalWebViewActivity.Builder.create(getContext())
                        .setUrl(articleUrl)
                        .setWebTipShare()
                        .setTitle(titleView.getText().toString())
                        .setWebTipId(id)
                        .startActivity();
            }
        });

        titleView.setText(title);
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SocialSharer(getContext()).share(
                        getContext().getText(R.string.share_chooser_tip_title),
                        Uri.parse(articleUrl)
                );
            }
        });

    }

    @Override
    protected void populateModel() {
        Tip tip = (Tip) adapter.getController().getObject(BotObjectType.TIP);
        if (tip != null) {
            dataModel.values.put(BotParamType.TIP_ID.getKey(), tip.getId());
            dataModel.values.put(BotParamType.TIP_TITLE.getKey(), tip.getTitle());
            dataModel.values.put(BotParamType.TIP_IMAGE_URL.getKey(), tip.getCoverImageUrl());
            dataModel.values.put(BotParamType.TIP_ARTICLE_URL.getKey(), tip.getArticleUrl());
        }
    }
}
