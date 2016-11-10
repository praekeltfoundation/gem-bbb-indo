package com.nike.dooit.views.main.fragments.tip;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_tip_image)
    SimpleDraweeView imageView;

    @BindView(R.id.card_tip_title)
    TextView titleView;

    @BindView(R.id.card_tip_fav)
    ImageView favView;

    @BindView(R.id.card_tip_share)
    ImageView shareView;

    public TipViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.card_tip_title)
    public void startArticle(View view) {
        Toast.makeText(view.getContext(), titleView.getText().toString() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.card_tip_fav)
    public void favouriteTip(View view) {
        Toast.makeText(view.getContext(), titleView.getText().toString() + " faved", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.card_tip_share)
    public void shareTip(View view) {
        Toast.makeText(view.getContext(), titleView.getText().toString() + " shared", Toast.LENGTH_SHORT).show();
    }

    public void setImageUri(Uri uri) {
        imageView.setImageURI(uri);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(SpannableString title) {
        titleView.setText(title);
    }
}
