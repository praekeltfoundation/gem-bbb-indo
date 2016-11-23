package org.gem.indo.dooit.views.main.fragments.tip.filters;

import android.widget.Filter;

import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsAutoCompleteAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/15.
 */

public class TipsAutoCompleteFilter extends Filter {

    private TipsAutoCompleteAdapter adapter;

    public TipsAutoCompleteFilter(TipsAutoCompleteAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (constraint != null) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();
            HashSet<String> closedSet = new HashSet<>();
            String c = constraint.toString().toLowerCase();

            for (Tip tip : adapter.getAllTips()) {
                if (tip.getTitle().toLowerCase().contains(c)
                        && !closedSet.contains(tip.getTitle())) {
                    suggestions.add(tip.getTitle());
                    closedSet.add(tip.getTitle());
                }
                for (String tag : tip.getTags())
                    if (tag.toLowerCase().contains(c) && !closedSet.contains(tag)) {
                        suggestions.add(tag);
                        closedSet.add(tag);
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
        adapter.setNotifyOnChange(false);
        adapter.clear();

        if (results.values != null && results.count > 0) {
            //  Avoids cast warning
            List<?> strings = (List<?>) results.values;
            for (Object obj : strings)
                if (obj instanceof String)
                    adapter.add((String) obj);
        } else {
            adapter.clear();
        }

        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
    }
}
