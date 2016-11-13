package com.nike.dooit.views.main.fragments.tip.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.helpers.Persisted;
import com.nike.dooit.models.Tip;
import com.nike.dooit.views.main.fragments.tip.TipViewHolder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipsListAdapter extends RecyclerView.Adapter<TipViewHolder> implements Filterable, TipsAdapter {

    private Context context;
    private List<Tip> tipsAll = new ArrayList<>();
    private TipsFilter filter;

    @Inject
    TipManager tipManager;

    @Inject
    Persisted persisted;

    public TipsListAdapter(DooitApplication application) {
        super();
        application.component.inject(this);
        this.context = application.getApplicationContext();
    }

    public TipsListAdapter(DooitApplication application, List<Tip> tips) {
        this(application);
        this.tipsAll = tips;
    }

    @Override
    public int getItemCount() {
        return tipsAll.size();
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TipViewHolder holder, int position) {
        Tip tip = tipsAll.get(position);

        // Id
        holder.setId(tip.getId());

        // Title
        String readMore = context.getString(R.string.tips_card_read_more);

        SpannableString st = new SpannableString(tip.getTitle() + " " + readMore);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.8f);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ResourcesCompat.getColor(
                context.getResources(), R.color.grey, context.getTheme()));
        int start = tip.getTitle().length();
        int end = start + 1 + readMore.length(); // Add 1 for space

        st.setSpan(sizeSpan, start, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        st.setSpan(colorSpan, start, end, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

        holder.setTitle(st);

        // Image
        if (tip.hasCoverImageUrl()) {
            Uri uri = Uri.parse(tip.getCoverImageUrl());
            holder.setImageUri(uri);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    @Override
    public List<Tip> getAllTips() {
        return tipsAll;
    }

    @Override
    public void updateAllTips(List<Tip> tips) {
        tipsAll.clear();
        tipsAll.addAll(tips);
        notifyDataSetChanged();
    }

    public List<Tip> getTips() {
        return tipsAll;
    }
}
