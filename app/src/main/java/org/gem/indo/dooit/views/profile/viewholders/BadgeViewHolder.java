package org.gem.indo.dooit.views.profile.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    public BadgeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(final Badge badge) {
        reset();

        // Text
        textView.setText(badge.getName());

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
