package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.enums.BotMessageType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The view holder for a single Goal item in the Goal Gallery Carousel.
 * <p>
 * Created by Wimpie Victor on 2016/11/24.
 */

public class AnswerGoalGalleryItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_carousel_card_image)
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_carousel_card_title)
    TextView title;

    @BindView(R.id.item_view_bot_carousel_card_select)
    TextView select;

    private HashtagView.TagsClickListener listener;
    private Answer dataModel;

    public AnswerGoalGalleryItemViewHolder(View itemView, HashtagView.TagsClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = listener;
    }

    public void populate(final GoalPrototype prototype, Answer model) {
        image.setImageURI(prototype.getImageUrl());
        title.setText(prototype.getName());
        dataModel = model;
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer answer = new Answer();
                answer.setName(dataModel.getName());
                answer.setType(BotMessageType.IMAGE);
                answer.setValue(prototype.getImageUrl());
                answer.put("prototype", Long.toString(prototype.getId()));
                answer.put("name", prototype.getName());
                answer.put("image_url", prototype.getImageUrl());
                answer.setNext(dataModel.getNextOnFinish());
                answer.setRemoveOnSelect(dataModel.getName());
                answer.setText(null);
                listener.onItemClicked(answer);
            }
        });
    }
}
