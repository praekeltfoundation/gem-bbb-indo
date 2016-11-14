package com.nike.dooit.views.main.fragments.tip.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.nike.dooit.R;
import com.nike.dooit.models.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/12.
 */

public class TipsAutoCompleteAdapter extends ArrayAdapter<Tip> implements TipsAdapter {

    private List<Tip> tipsAll = new ArrayList<>();

    public TipsAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_tips_item, parent, false);
        }
        Tip tip = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.list_tips_item_text_view);
        textView.setText(tip.getTitle());

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
        return new TipsFilter(this);
    }
}
