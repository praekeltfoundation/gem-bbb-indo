package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.gem.indo.dooit.models.GoalPrototype;

import butterknife.ButterKnife;

/**
 * The view holder for a single Goal item in the Goal Gallery Carousel.
 *
 * Created by Wimpie Victor on 2016/11/24.
 */

public class AnswerGoalGalleryItemViewHolder extends RecyclerView.ViewHolder {



    public AnswerGoalGalleryItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(GoalPrototype prototype) {
        // Image

        // Title
    }
}
