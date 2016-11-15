package com.nike.dooit.views.main.fragments.tip.adapters;

import android.widget.Filter;

import com.nike.dooit.models.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/12.
 */

public class TipsFilter extends Filter {

    private TipsAutoCompleteAdapter adapter;

    public TipsFilter(TipsAutoCompleteAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (constraint != null) {
            FilterResults results = new FilterResults();
            List<Tip> suggestions = new ArrayList<>();
            String c = constraint.toString().toLowerCase();

            for (Tip tip : adapter.getAllTips()) {
                String title = tip.getTitle().toLowerCase();
                if (title.contains(c)) {
                    suggestions.add(tip);
                    continue;
                }
                for (String tag : tip.getTags()) {
                    if (tag.toLowerCase().contains(c)) {
                        suggestions.add(tip);
                        break;
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        } else {
            return new FilterResults();
        }
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results != null && results.count > 0) {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            //  Avoids cast warning
            List<?> tips = (List<?>) results.values;
            for (Object obj : tips) {
                if (obj instanceof Tip) {
                    //adapter.add((Tip) obj);
                }
            }
        } else if (constraint == null) {
            // No filter
            //adapter.addAll(adapter.getAllTips());
        }
    }
}
