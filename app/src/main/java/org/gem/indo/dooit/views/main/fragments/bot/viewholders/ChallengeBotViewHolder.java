package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/12/09.
 */

public class ChallengeBotViewHolder extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_challenge)
    View background;

    @BindView(R.id.item_view_bot_challenge_image)
    SimpleDraweeView imageView;

    @BindView(R.id.item_view_bot_challenge_image_sub_title)
    TextView subTitleView;

    @BindView(R.id.item_view_bot_challenge_image_title)
    TextView titleView;

    private BotAdapter botAdapter;

    public ChallengeBotViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;

        background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);
    }

    @Override
    protected void populateModel() {
        BaseChallenge challenge = (BaseChallenge) botAdapter.getController().getObject(BotObjectType.CHALLENGE);
    }
}
