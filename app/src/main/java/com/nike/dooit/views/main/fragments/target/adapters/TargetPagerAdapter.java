package com.nike.dooit.views.main.fragments.target.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.R;
import com.nike.dooit.models.Goal;
import com.nike.dooit.views.custom.ArcProgressBar;

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
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_target_progress, container, false);

        ((ArcProgressBar) layout.findViewById(R.id.fragment_target_progress_view)).setProgress((int) ((goals.get(position).getValue() / goals.get(position).getTarget()) * 100));
        ((SimpleDraweeView) layout.findViewById(R.id.fragment_target_progress_image)).setImageURI(goals.get(position).getImage());
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
}