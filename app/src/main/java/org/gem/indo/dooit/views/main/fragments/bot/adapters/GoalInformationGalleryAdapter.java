package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalInformationGalleryItemViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Reinhardt on 2017/03/07.
 */

public class GoalInformationGalleryAdapter extends RecyclerView.Adapter<GoalInformationGalleryItemViewHolder> {
    private List<Goal> goals;
    private HashtagView.TagsClickListener listener;
    private Context context;
    private Node dataModel;

    public GoalInformationGalleryAdapter(HashtagView.TagsClickListener listener, Context context) {
        this.goals = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return goals == null ? 0 : goals.size();
    }

    @Override
    public GoalInformationGalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_bot_goal_info, parent, false);
        return new GoalInformationGalleryItemViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(GoalInformationGalleryItemViewHolder holder, int position) {
        holder.populate(goals.get(position), dataModel, listener);
    }

    public void setDataModel(Node dataModel) {
        this.dataModel = dataModel;
    }

    public void replace(Collection<Goal> goals) {
        this.goals.clear();
        if(goals != null) {
            this.goals.addAll(goals);
            notifyDataSetChanged();
        }
    }
}
