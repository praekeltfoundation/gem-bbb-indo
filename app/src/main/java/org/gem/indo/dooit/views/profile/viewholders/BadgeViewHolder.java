package org.gem.indo.dooit.views.profile.viewholders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.Badge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        textView.setText(badge.getName());
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClick(badge);
            }
        });
    }

    public void onShareClick(Badge badge) {

    }
}
