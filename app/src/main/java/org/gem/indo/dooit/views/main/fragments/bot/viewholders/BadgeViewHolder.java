package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.exceptions.BotCallbackRequired;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import javax.inject.Inject;

import butterknife.BindString;
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
    TextView shareView;

    @BindView(R.id.item_challenge_title)
    TextView challengeTitle;

    @BindString(R.string.challenge)
    String challenge;

    @Inject
    Persisted persisted;

    private BotAdapter botAdapter;

    public BadgeViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ButterKnife.bind(this, itemView);
        ((DooitApplication) itemView.getContext().getApplicationContext()).component.inject(this);

        if (!botAdapter.hasController())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));

        // Must assign programmatically for Support Library to wrap before API 21
        separator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_separator));
        background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));

        if(persisted.hasConvoWinner(BotType.CHALLENGE_WINNER)) {
            challengeTitle.setText(persisted.loadWinningChallenge(BotType.CHALLENGE_WINNER).getName() + " " + challenge);
            challengeTitle.setVisibility(View.VISIBLE);
        }
        else
            challengeTitle.setVisibility(View.GONE);
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        String title = dataModel.values.getString(BotParamType.BADGE_NAME.getKey());
        Uri imageUri = null;
        if (!TextUtils.isEmpty(dataModel.values.getString(BotParamType.BADGE_IMAGE_URL.getKey())))
            imageUri = Uri.parse(dataModel.values.getString(BotParamType.BADGE_IMAGE_URL.getKey()));
        String socialUrl = dataModel.values.getString(BotParamType.BADGE_SOCIAL_URL.getKey());

        if (imageUri != null)
            setImageUri(image, imageUri);

        if (!TextUtils.isEmpty(socialUrl)) {
            final Uri socialUri = Uri.parse(socialUrl);
            shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SocialSharer(getContext()).share(
                            getContext().getText(R.string.share_chooser_badge_title),
                            socialUri
                    );
                }
            });
        }
    }

    @Override
    protected void populateModel() {
        if (botAdapter.hasController()) {
            Badge badge = (Badge) botAdapter.getController().getObject(BotObjectType.BADGE);
            if (badge != null) {
                dataModel.values.put(BotParamType.BADGE_NAME.getKey(), badge.getName());
                dataModel.values.put(BotParamType.BADGE_IMAGE_URL.getKey(), badge.getImageUrl());
                dataModel.values.put(BotParamType.BADGE_SOCIAL_URL.getKey(), badge.getSocialUrl());
            }
        }
    }
}
