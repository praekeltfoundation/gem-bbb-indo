package org.gem.indo.dooit.views.main.fragments.tip.filters;

import android.widget.Filter;

import org.gem.indo.dooit.helpers.interfaces.VariableChangeListener;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.adapters.TipsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/12.
 */

public class TipsListFilter extends Filter {

    private TipsListAdapter adapter;
    private VariableChangeListener variableChangeListener;

    public TipsListFilter(TipsListAdapter adapter) {
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
        adapter.clearFiltered();
        if (results != null && results.count > 0) {
            //  Avoids cast warning
            List<?> tips = (List<?>) results.values;
            for (Object obj : tips) {
                if (obj instanceof Tip) {
                    adapter.addFiltered((Tip) obj);
                }
            }
        } else if (constraint == null) {
            // No filter
            adapter.clearFiltered();
        }
        adapter.notifyDataSetChanged();
        variableChangeListener.onVariableChanged(adapter.getItemCount());
    }

    public void setVariableChangeListener(VariableChangeListener variableChangeListener) {
        this.variableChangeListener = variableChangeListener;
    }
}
