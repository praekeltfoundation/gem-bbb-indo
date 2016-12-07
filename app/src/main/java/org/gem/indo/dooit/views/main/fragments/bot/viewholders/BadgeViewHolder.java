package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.Goal;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.exceptions.BotCallbackRequired;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class BadgeViewHolder extends BaseBotViewHolder<Node> {

    private static final String TAG = BadgeViewHolder.class.getName();

    @BindView(R.id.item_view_bot_badge)
    View background;

    @BindView(R.id.item_view_bot_badge_image)
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_badge_separator)
    View separator;

    @BindView(R.id.item_view_bot_badge_share)
    TextView share;

    private BotAdapter botAdapter;

    public BadgeViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ButterKnife.bind(this, itemView);

        if (!botAdapter.hasCallback())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));

        // Must assign programmatically for Support Library to wrap before API 21
        separator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_separator));
        background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        Goal goal = (Goal) botAdapter.getCallback().getObject();
        if (goal.hasNewBadges()) {
            Badge badge = goal.getNewBadges().get(0);
            image.setImageURI(badge.getImageUrl());
        }
    }
}