package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reinhardt on 2017/02/20.
 */

public class DummyViewHolder extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_text_container)
    View container;

    @BindView(R.id.item_view_bot_text)
    TextView textView;

    @BindView(R.id.item_view_bot_icon)
    ImageView botIcon;

    @BindView(R.id.item_view_bot_text_tail)
    View tailView;

    BotAdapter botAdapter;

    public DummyViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ButterKnife.bind(this, itemView);
        container.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        botIcon.setVisibility(View.GONE);
        tailView.setVisibility(View.GONE);
    }

    @Override
    public void populate(Node model) {
        super.populate(model);
    }

    @Override
    protected void populateModel() {
    }
}
