package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;
import com.nike.dooit.R;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerImageViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_bot_image)
    SimpleDraweeView simpleDraweeView;
    BotAdapter botAdapter;
    HashtagView.TagsClickListener tagsClickListener;

    public AnswerImageViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Answer model) {
        this.dataModel = model;
        simpleDraweeView.setImageURI(Uri.parse(model.getValue()));
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
