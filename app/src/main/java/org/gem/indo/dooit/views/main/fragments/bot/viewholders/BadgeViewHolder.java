package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.view.View;

import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class BadgeViewHolder extends BaseBotViewHolder<Node> {

    private BotAdapter botAdapter;

    public BadgeViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
    }

    @Override
    public void populate(Node model) {
        super.populate(model);
    }
}
