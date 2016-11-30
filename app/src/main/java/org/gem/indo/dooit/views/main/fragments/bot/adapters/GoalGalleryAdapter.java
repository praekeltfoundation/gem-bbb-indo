package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalGalleryItemViewHolder;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/24.
 */

public class GoalGalleryAdapter extends RecyclerView.Adapter<GoalGalleryItemViewHolder> {

    private List<GoalPrototype> prototypes;
    private HashtagView.TagsClickListener listener;
    private Node dataModel;

    public GoalGalleryAdapter(List<GoalPrototype> prototypes, HashtagView.TagsClickListener listener) {
        this.prototypes = prototypes;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return prototypes.size();
    }

    @Override
    public GoalGalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_bot_carousel_card, parent, false);
        return new GoalGalleryItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(GoalGalleryItemViewHolder holder, int position) {
        holder.populate(prototypes.get(position), dataModel);
    }

    public void setDataModel(Node dataModel) {
        this.dataModel = dataModel;
    }
}
