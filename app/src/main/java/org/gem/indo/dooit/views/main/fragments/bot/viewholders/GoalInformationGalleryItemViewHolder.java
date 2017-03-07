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
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.goal.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reinhardt on 2017/03/07.
 */

public class GoalInformationGalleryItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_carousel_card_goal)
    View background;

    @BindView(R.id.item_view_bot_carousel_card_image_goal)
    SimpleDraweeView image;

    @BindView(R.id.item_view_bot_carousel_card_title_goal)
    TextView title;

    @BindView(R.id.item_view_bot_carousel_card_title_amount)
    TextView amount;

    @BindView(R.id.item_view_bot_carousel_card_separator1_goal)
    View separator1;

    @BindView(R.id.item_view_bot_carousel_card_separator2_goal)
    View separator2;

    @BindView(R.id.item_view_bot_carousel_card_select_goal)
    TextView select;

    public GoalInformationGalleryItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // Must assign programmatically for Support Library to wrap before API 21
        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
        separator1.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
        separator2.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
    }

    public void populate(final Goal goal, final HashtagView.TagsClickListener listener) {
        reset();

        if (goal.hasImageUrl())
            DraweeHelper.setProgressiveUri(image, Uri.parse(goal.getImageUrl()));
        title.setText(goal.getName());
        amount.setText(goal.getValueFormatted());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo When an item is clicked the app should navigate to that goal
            }
        });
    }

    private void reset() {
        title.setText("");
        itemView.setOnClickListener(null);
    }
}
