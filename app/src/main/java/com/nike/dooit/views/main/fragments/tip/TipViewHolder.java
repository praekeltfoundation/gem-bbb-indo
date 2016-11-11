package com.nike.dooit.views.main.fragments.tip;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.TipManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Wimpie Victor on 2016/11/07.
 */

public class TipViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "TipViewHolder";

    @BindView(R.id.card_tip_image)
    SimpleDraweeView imageView;

    @BindView(R.id.card_tip_title)
    TextView titleView;

    @BindView(R.id.card_tip_fav)
    ImageView favView;

    @BindView(R.id.card_tip_share)
    ImageView shareView;

    int id;
    TipManager tipManager;

    public TipViewHolder(View itemView, TipManager tipManager) {
        super(itemView);
        this.tipManager = tipManager;
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.card_tip_title)
    public void startArticle(View view) {
        Toast.makeText(view.getContext(), titleView.getText().toString() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.card_tip_fav)
    public void favouriteTip(final View view) {
        tipManager.favourite(id, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                
            }
        }).subscribe();
        Toast.makeText(view.getContext(), titleView.getText().toString() + " faved", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.card_tip_share)
    public void shareTip(View view) {
        Toast.makeText(view.getContext(), titleView.getText().toString() + " shared", Toast.LENGTH_SHORT).show();
    }

    public void setImageUri(Uri uri) {
        imageView.setImageURI(uri);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(SpannableString title) {
        titleView.setText(title);
    }
}
