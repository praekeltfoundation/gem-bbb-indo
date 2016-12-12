package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.goal.Goal;
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

        if (!botAdapter.hasController())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));

        // Must assign programmatically for Support Library to wrap before API 21
        separator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_separator));
        background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        String title = dataModel.values.getString(BotParamType.BADGE_NAME.getKey());
        Uri imageUri = Uri.parse(dataModel.values.getString(BotParamType.BADGE_IMAGE_URL.getKey()));

        setImageUri(image, imageUri);
    }
    @Override
    protected void populateModel() {
        // Done in controller
    }
}
