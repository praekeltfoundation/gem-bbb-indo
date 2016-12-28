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

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Rudolph Jacobs on 2016-12-05.
 */

public class FeedbackTypeAdapter extends ArrayAdapter<FeedbackType> {

    /**
     * The selected item has its own distinct resource, because it is more constrained in the
     * Spinner's view. This prevents text from being cut off by excessive padding.
     */
    @LayoutRes
    private int selectedItemRes;

    @LayoutRes
    private int dropdownItemRes;

    public FeedbackTypeAdapter(@NonNull Context context, @LayoutRes int selectedItemRes,
                               @LayoutRes int dropdownItemRes, Collection<FeedbackType> options) {
        super(context, selectedItemRes);
        this.selectedItemRes = selectedItemRes;
        this.dropdownItemRes = dropdownItemRes;
        this.addAll(options);
    }

    public FeedbackTypeAdapter(@NonNull Context context, @LayoutRes int selectedItemRes,
                               @LayoutRes int dropdownItemRes, FeedbackType[] options) {
        this(context, selectedItemRes, dropdownItemRes, Arrays.asList(options));
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
        return getCustomView(position, convertView, parent, dropdownItemRes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent, selectedItemRes);
    }

    private View getCustomView(int position, View convertView, @NonNull ViewGroup parent,
                               @LayoutRes int resource) {
        View v;
        if (convertView == null) {
            v = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        } else {
            v = convertView;
        }
        TextView tv = (TextView) v.findViewById(R.id.item_text);
        FeedbackType ft = getItem(position);
        if (ft != null) {
            tv.setText(ft.getText());
        }
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        return v;
    }
}
