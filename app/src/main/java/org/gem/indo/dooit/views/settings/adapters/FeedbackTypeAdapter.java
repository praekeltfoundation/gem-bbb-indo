package org.gem.indo.dooit.views.settings.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.enums.FeedbackType;

import java.util.Collection;

/**
 * Created by Rudolph Jacobs on 2016-12-05.
 */

public class FeedbackTypeAdapter extends ArrayAdapter<FeedbackType> {
    private Context context;

    public FeedbackTypeAdapter(@NonNull Context context, @LayoutRes int layoutRes, Collection<FeedbackType> options) {
        super(context, layoutRes);
        this.addAll(options);
        this.context = context;
    }

    public FeedbackTypeAdapter(@NonNull Context context, @LayoutRes int layoutRes, FeedbackType[] options) {
        super(context, layoutRes);
        this.addAll(options);
        this.context = context;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        FeedbackType ft = getItem(position);
        if (ft == null) return -1;
        return ft.getValue();
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

    private View getCustomView(int position, View convertView, @NonNull ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.item_view_spinner, parent, false);
        } else {
            v = convertView;
        }
        TextView tv = (TextView) v.findViewById(R.id.item_text);
        FeedbackType ft = (FeedbackType) getItem(position);
        if (ft != null) {
            tv.setText(ft.getText());
        }
        tv.setTextColor(ContextCompat.getColor(context, R.color.black));
        return v;
    }
}
