package org.gem.indo.dooit.views.settings.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.enums.FeedbackType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Rudolph Jacobs on 2016-12-05.
 */

public class FeedbackTypeAdapter extends ArrayAdapter<FeedbackType> {
    private Context context;

    private List<FeedbackType> options;

    public FeedbackTypeAdapter(@NonNull Context context, @LayoutRes int layoutRes, List<FeedbackType> options) {
        super(context, layoutRes);
        if (options == null) {
            this.options = Arrays.asList(FeedbackType.values());
        } else {
            this.options = options;
        }
        this.context = context;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        FeedbackType ft = options.get(position);
        if (ft == null) return -1;
        return ft.getValue();
    }

    @Nullable
    @Override
    public FeedbackType getItem(int position) {
        return options == null ? null : options.get(position);
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return options.size();
    }

    private View getCustomView(int position, View convertView, @NonNull ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        } else {
            v = convertView;
        }
        TextView tv = (TextView) v.findViewById(R.id.item_text);
        tv.setText(options.get(position).getText());
        tv.setTextColor(ContextCompat.getColor(context, R.color.black));
        return v;
    }
}
