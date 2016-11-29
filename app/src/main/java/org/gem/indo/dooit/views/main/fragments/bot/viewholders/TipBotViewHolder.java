package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.bot.Node;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/11/29.
 */

public class TipBotViewHolder extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_tip_image)
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_tip_title)
    TextView title;

    @Inject
    Persisted persisted;

    HashtagView.TagsClickListener listener;

    public TipBotViewHolder(View itemView, HashtagView.TagsClickListener listener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        this.listener = listener;
    }

    @Override
    public void populate(Node model) {
        dataModel= model;
    }
}
