package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.GoalPrototype;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotMessageType;

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

    private HashtagView.TagsClickListener listener;
    private Node dataModel;

    public GoalGalleryItemViewHolder(View itemView, HashtagView.TagsClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = listener;

        // Must assign programmatically for Support Library to wrap before API 21
        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
        separator.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
    }

    public void populate(final GoalPrototype prototype, Node model) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(prototype.getImageUrl()))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(image.getController())
                .build();

        image.setController(controller);
        title.setText(prototype.getName());
        dataModel = model;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer answer = new Answer();
                answer.setName(dataModel.getAnswerName());
                answer.setType(BotMessageType.IMAGE);
                answer.setValue(prototype.getImageUrl());
                answer.put("prototype", Long.toString(prototype.getId()));
                answer.put("name", prototype.getName());
                answer.put("image_url", prototype.getImageUrl());
                answer.setNext(dataModel.getNext());
                answer.setRemoveOnSelect(dataModel.getName());
                answer.setText(null);
                listener.onItemClicked(answer);
            }
        });
    }
}
