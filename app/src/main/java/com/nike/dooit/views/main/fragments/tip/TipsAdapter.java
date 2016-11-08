package com.nike.dooit.views.main.fragments.tip;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nike.dooit.R;
import com.nike.dooit.models.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipsAdapter extends RecyclerView.Adapter<TipViewHolder> {

    private List<Tip> tips = new ArrayList<>();

    public TipsAdapter() {
        super();
        // Empty constructor
    }

    public TipsAdapter(List<Tip> tips) {
        this();
        this.tips = tips;
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TipViewHolder holder, int position) {
        Tip tip = tips.get(position);
        holder.setTitle(tip.getTitle());
    }

    public void updateTips(List<Tip> tips) {
        this.tips = tips;
        notifyDataSetChanged();
    }
}
