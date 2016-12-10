package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.tip.TipArticleActivity;
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
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_tip_title)
    TextView title;

    @BindView(R.id.item_view_bot_tip_share)
    TextView share;

    @Inject
    Persisted persisted;

    HashtagView.TagsClickListener listener;

    public TipBotViewHolder(View itemView, HashtagView.TagsClickListener listener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        this.listener = listener;

        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        // If no Tip is saved, the view will still display, but be empty
        if (persisted.hasConvoTip()) {
            final Tip tip = persisted.loadConvoTip();
            image.setImageURI(tip.getCoverImageUrl());

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Answer answer = new Answer();
                    answer.setName(dataModel.getName());
                    answer.setType(BotMessageType.BLANKANSWER);
                    answer.setValue(tip.getArticleUrl());
                    answer.setText(null);
                    listener.onItemClicked(answer);

                    MainActivity activity = (MainActivity) getContext();
                    if (activity != null)
                        activity.startPage(MainViewPagerPositions.TIPS);

                    Toast.makeText(getContext(), String.format(openingText, tip.getTitle()), Toast.LENGTH_LONG).show();
                    MinimalWebViewActivity.Builder.create(getContext())
                            .setUrl(tip.getArticleUrl())
                            .setNoCaret()
                            .startActivity();
                }
            });

            title.setText(tip.getTitle());
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Share tip on social media
                }
            });
        }
    }

    @Override
    protected void populateModel() {

    }
}
