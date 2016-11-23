package org.gem.indo.dooit.views.main.fragments.tip.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.views.main.fragments.tip.filters.TipsAutoCompleteFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/12.
 */

public class TipsAutoCompleteAdapter extends ArrayAdapter<String> implements TipsAdapter {

    private List<Tip> tipsAll = new ArrayList<>();
    private TipsAutoCompleteFilter filter;

    public TipsAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(org.gem.indo.dooit.R.layout.item_tips_search_suggestion, parent, false);
        }
        String suggestion = getItem(position);

        TextView textView = (TextView) convertView.findViewById(org.gem.indo.dooit.R.id.list_tips_item_text_view);
        textView.setText(suggestion);

        return convertView;
    }

    public void updateAllTips(List<Tip> tips) {
        tipsAll.clear();
        tipsAll.addAll(tips);
    }

    public List<Tip> getAllTips() {
        return tipsAll;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new TipsAutoCompleteFilter(this);
        return filter;
    }
}
