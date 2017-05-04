package org.gem.indo.dooit.views.profile.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.social.SocialSharer;
import org.gem.indo.dooit.models.Badge;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class BadgeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_profile_achievement_text_text_view)
    TextView textView;

    @BindView(R.id.activity_profile_achievement_item_share)
    ImageView shareView;

    @BindView(R.id.activity_profile_achievement_icon_image_view)
    SimpleDraweeView badgeView;

    public BadgeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(final Badge badge) {
        reset();

        // Text
        textView.setText(badge.getName());

        if (badge.getImageUrl() != null) {
            badgeView.setImageURI(badge.getImageUrl());
        } else {
            badgeView.setImageResource(R.drawable.ic_d_badge);
            badgeView.setScaleType(SimpleDraweeView.ScaleType.CENTER_INSIDE);
        }

        // Share
        if (badge.hasSocialUrl()) {
            shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SocialSharer(itemView.getContext()).share(
                            getContext().getText(R.string.share_chooser_badge_title),
                            Uri.parse(badge.getSocialUrl())
                    );
                }
            });
        }
    }

    private void reset() {
        shareView.setOnClickListener(null);
    }

    private Context getContext() {
        return itemView.getContext();
    }
}
