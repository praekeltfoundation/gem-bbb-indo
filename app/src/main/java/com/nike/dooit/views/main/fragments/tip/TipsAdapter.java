package com.nike.dooit.views.main.fragments.tip;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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

    private Context context;
    private List<Tip> tips = new ArrayList<>();

    public TipsAdapter(Context context) {
        super();
        this.context = context;
    }

    public TipsAdapter(Context context, List<Tip> tips) {
        this(context);
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
    }

    public void updateTips(List<Tip> tips) {
        this.tips = tips;
        notifyDataSetChanged();
    }
}
