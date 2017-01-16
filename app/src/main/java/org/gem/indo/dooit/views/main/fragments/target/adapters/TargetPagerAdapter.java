package org.gem.indo.dooit.views.main.fragments.target.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.custom.ArcProgressBar;

import java.util.List;

/**
 * Created by herman on 2016/11/12.
 */

public class TargetPagerAdapter extends PagerAdapter {

    List<Goal> goals;

    public TargetPagerAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_target_arc_progress, container, false);

        int progress = (int) ((goals.get(position).getValue() / goals.get(position).getTarget()) * 100);
        // Progress is clamped to 0 <= progress <= 100
        ((ArcProgressBar) layout.findViewById(R.id.progress_arc)).setProgress(Math.min(Math.max(0, progress), 100));
        ((SimpleDraweeView) layout.findViewById(R.id.progress_image)).setImageURI(goals.get(position).getImageUrl());
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return goals.size();
    }

    public void updateGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    /*
        This was to fix the fact fragments were not updating when moving to a fragment directly next to it
        issue https://github.com/praekeltfoundation/gem-bbb-indo/pull/389

        This solution forces the position to become POSITION_NONE and as a result forces the fragments to
        be reloaded thus seeing the updates between the two
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}