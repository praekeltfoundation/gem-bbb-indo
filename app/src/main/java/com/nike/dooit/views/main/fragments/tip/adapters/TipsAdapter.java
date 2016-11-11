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

import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.models.Tip;
import com.nike.dooit.views.main.fragments.tip.TipViewHolder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipsAdapter extends RecyclerView.Adapter<TipViewHolder> {

    private Context context;
    private List<Tip> tips = new ArrayList<>();

    @Inject
    TipManager tipManager;

    public TipsAdapter(DooitApplication application) {
        super();
        application.component.inject(this);
        this.context = application.getApplicationContext();
    }

    public TipsAdapter(DooitApplication application, List<Tip> tips) {
        this(application);
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
        return new TipViewHolder(view, tipManager);
    }

    @Override
    public void onBindViewHolder(TipViewHolder holder, int position) {
        Tip tip = tips.get(position);

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

        if (position % 3 == 0) {

        }
    }

    public void updateTips(List<Tip> tips) {
        this.tips = tips;
        notifyDataSetChanged();
    }

    public List<Tip> getTips() {
        return tips;
    }
}
