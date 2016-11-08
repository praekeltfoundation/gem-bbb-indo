package com.nike.dooit.views.main.fragments.tip;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.nike.dooit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_tip_title)
    TextView titleView;

    public TipViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(SpannableString title) {
        titleView.setText(title);
    }
}
