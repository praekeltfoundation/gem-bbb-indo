package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerGoalGalleryItemViewHolder;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/24.
 */

public class GoalGalleryAdapter extends RecyclerView.Adapter<AnswerGoalGalleryItemViewHolder> {

    private List<GoalPrototype> prototypes;

    public GoalGalleryAdapter(List<GoalPrototype> prototypes) {
        this.prototypes = prototypes;
    }

    @Override
    public int getItemCount() {
        return prototypes.size();
    }

    @Override
    public AnswerGoalGalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_bot_carousel_item, parent, false);
        return new AnswerGoalGalleryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnswerGoalGalleryItemViewHolder holder, int position) {
        holder.populate(prototypes.get(position));
    }
}
