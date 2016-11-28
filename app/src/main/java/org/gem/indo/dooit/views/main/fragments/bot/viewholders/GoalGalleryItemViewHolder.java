package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.GoalPrototype;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The view holder for a single Goal item in the Goal Gallery Carousel.
 *
 * Created by Wimpie Victor on 2016/11/24.
 */

public class GoalGalleryItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_carousel_card_image)
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_carousel_card_title)
    TextView title;

    public GoalGalleryItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(GoalPrototype prototype) {
        image.setImageURI(prototype.getImageUrl());
        title.setText(prototype.getName());
    }
}
