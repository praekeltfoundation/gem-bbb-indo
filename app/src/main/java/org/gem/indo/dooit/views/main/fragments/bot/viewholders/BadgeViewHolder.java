package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.view.View;

import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.exceptions.BotCallbackRequired;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class BadgeViewHolder extends BaseBotViewHolder<Node> {

    private static final String TAG = BadgeViewHolder.class.getName();

    private BotAdapter botAdapter;

    public BadgeViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;

        if (!botAdapter.hasCallback())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        Goal goal = (Goal) botAdapter.getCallback().getObject();

    }
}
