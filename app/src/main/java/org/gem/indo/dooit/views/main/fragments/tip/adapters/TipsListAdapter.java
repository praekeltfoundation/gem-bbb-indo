package org.gem.indo.dooit.views.main.fragments.tip.adapters;

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

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.filters.TipsListFilter;
import org.gem.indo.dooit.views.main.fragments.tip.viewholders.TipViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipsListAdapter extends RecyclerView.Adapter<TipViewHolder> implements Filterable, TipsAdapter {

    private Context context;
    private List<Tip> tipsFiltered = new ArrayList<>();
    private List<Tip> tipsAll = new ArrayList<>();
    private TipsListFilter filter;

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
        this.tipsFiltered.addAll(tips);
        this.tipsAll.addAll(tips);
    }

    @Override
    public int getItemCount() {
        return tipsFiltered.size();
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TipViewHolder holder, int position) {
        Tip tip = tipsFiltered.get(position);

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

        // Article
        holder.setArticleUrl(tip.getArticleUrl());

        // Image
        if (tip.hasCoverImageUrl()) {
            Uri uri = Uri.parse(tip.getCoverImageUrl());
            holder.setImageUri(uri);
        }

        // Favourite
        holder.setFavourite(tip.isFavourite());

        // Tags
        holder.clearTags();
        holder.addTags(tip.getTags());
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new TipsListFilter(this);
        return filter;
    }

    public void clearFiltered() {
        tipsFiltered.clear();
    }

    /**
     * Sets filtered tips to all tips.
     */
    public void resetFiltered() {
        clearFiltered();
        addAllFiltered(tipsAll);
        notifyDataSetChanged();
    }

    public void addFiltered(Tip tip) {
        tipsFiltered.add(tip);
    }

    public void addAllFiltered(Collection<Tip> tips) {
        tipsFiltered.addAll(tips);
    }

    public Tip getFiltered(int index) {
        return tipsFiltered.get(index);
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
}
