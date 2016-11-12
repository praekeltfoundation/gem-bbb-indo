package com.nike.dooit.views.main.fragments.tip;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nike.dooit.DooitApplication;
import com.nike.dooit.R;
import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.TipManager;
import com.nike.dooit.api.responses.EmptyResponse;
import com.nike.dooit.helpers.Persisted;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

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

    @Inject
    TipManager tipManager;

    @Inject
    Persisted persisted;

    int id;

    public TipViewHolder(View itemView) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @OnClick(R.id.card_tip_title)
    public void startArticle(View view) {
        Toast.makeText(view.getContext(), titleView.getText().toString() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.card_tip_fav)
    public void favouriteTip(View view) {
        tipManager.favourite(id, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<EmptyResponse>() {
            @Override
            public void call(EmptyResponse emptyResponse) {
                persisted.clearFavourites();
            }
        });
        Toast.makeText(getContext(), titleView.getText().toString() + " faved", Toast.LENGTH_SHORT).show();
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