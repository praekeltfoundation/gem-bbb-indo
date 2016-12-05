package org.gem.indo.dooit.views.profile.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.Badge;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class BadgeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_profile_achievement_text_text_view)
    TextView textView;

    public BadgeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(Badge badge) {
        textView.setText(badge.getName());
    }
}
