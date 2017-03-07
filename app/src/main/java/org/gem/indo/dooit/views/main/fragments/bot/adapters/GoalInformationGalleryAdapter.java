package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalGalleryItemViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalInformationGalleryItemViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Reinhardt on 2017/03/07.
 */

public class GoalInformationGalleryAdapter extends RecyclerView.Adapter<GoalInformationGalleryItemViewHolder> {
    private List<Goal> prototypes;
    private HashtagView.TagsClickListener listener;
    private Node dataModel;

    public GoalInformationGalleryAdapter(HashtagView.TagsClickListener listener) {
        this.prototypes = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return prototypes == null ? 0 : prototypes.size();
    }

    @Override
    public GoalInformationGalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_bot_carousel_card, parent, false);
        return new GoalInformationGalleryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalInformationGalleryItemViewHolder holder, int position) {
        holder.populate(prototypes.get(position), dataModel, listener);
    }

    public void setDataModel(Node dataModel) {
        this.dataModel = dataModel;
    }

    public void replace(Collection<Goal> prototypes) {
        this.prototypes.clear();
        this.prototypes.addAll(prototypes);
        notifyDataSetChanged();
    }
}
