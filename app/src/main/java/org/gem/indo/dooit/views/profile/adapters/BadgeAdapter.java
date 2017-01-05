package org.gem.indo.dooit.views.profile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.Badge;
import org.gem.indo.dooit.views.profile.viewholders.BadgeViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/12/04.
 */

public class BadgeAdapter extends RecyclerView.Adapter<BadgeViewHolder> {

    private List<Badge> badges = new ArrayList<>();

    public BadgeAdapter() {
    }

    public BadgeAdapter(List<Badge> badges) {
        addAll(badges);
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    @Override
    public BadgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_profile_achievement, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BadgeViewHolder holder, int position) {
        holder.populate(getItem(position));
    }

    public Badge getItem(int position) {
        return badges.get(position);
    }

    public void addAll(List<Badge> badges) {
        this.badges.addAll(badges);
        notifyDataSetChanged();
    }
}
