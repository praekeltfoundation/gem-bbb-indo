package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.models.goal.GoalPrototype;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The view holder for a single Goal item in the Goal Gallery Carousel.
 * <p>
 * Created by Wimpie Victor on 2016/11/24.
 */

public class GoalGalleryItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_carousel_card)
    View background;

    @BindView(R.id.item_view_bot_carousel_card_image)
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_carousel_card_title)
    TextView title;

    @BindView(R.id.item_view_bot_carousel_card_separator)
    View separator;

    @BindView(R.id.item_view_bot_carousel_card_select)
    TextView select;

    private Node dataModel;

    public GoalGalleryItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // Must assign programmatically for Support Library to wrap before API 21
        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
        separator.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
    }

    public void populate(final GoalPrototype prototype, Node model, final HashtagView.TagsClickListener listener) {
        reset();

        dataModel = model;

        if (prototype.hasImageUrl())
            DraweeHelper.setProgressiveUri(image, Uri.parse(prototype.getImageUrl()));
        title.setText(prototype.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer answer = new Answer();
                answer.setName(dataModel.getAnswerName());
                answer.setType(BotMessageType.IMAGE);
                answer.setInputKey(BotParamType.GOAL_PROTO);
                answer.setValue(prototype.getImageUrl());
                answer.values.put(BotParamType.GOAL_PROTO_ID.getKey(), prototype.getId());
                answer.values.put(BotParamType.GOAL_PROTO_NAME.getKey(), prototype.getName());
                answer.values.put(BotParamType.GOAL_PROTO_IMAGE_URL.getKey(), prototype.getImageUrl());
                answer.values.put(BotParamType.GOAL_PROTO_NUM_USERS_WITH_SIMILAR_GOALS.getKey(), prototype.getNumUsers());
                answer.setNext(dataModel.getNext());
                answer.setRemoveOnSelect(dataModel.getName());
                answer.setText(null);
                listener.onItemClicked(answer);
            }
        });
    }

    private void reset() {
        title.setText("");
        dataModel = null;
        itemView.setOnClickListener(null);
    }
}
