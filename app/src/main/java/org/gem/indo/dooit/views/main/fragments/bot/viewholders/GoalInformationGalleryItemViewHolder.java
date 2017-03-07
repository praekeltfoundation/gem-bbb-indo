package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.views.custom.ArcProgressBar;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reinhardt on 2017/03/07.
 */

public class GoalInformationGalleryItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_goal_info_current)
    TextView currentTextView;
    @BindView(R.id.item_view_bot_goal_info_title)
    TextView titleTextView;
    @BindView(R.id.item_view_bot_goal_info_total)
    TextView totalTextView;
    @BindView(R.id.progress_image)
    SimpleDraweeView image;
    @BindView(R.id.progress_arc)
    ArcProgressBar arcProgressBar;

    private Context context;

    public GoalInformationGalleryItemViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;

        itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bkg_carousel_card));

//        // Must assign programmatically for Support Library to wrap before API 21
//        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
//        separator1.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
//        separator2.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
    }

    public void populate(final Goal goal, final HashtagView.TagsClickListener listener) {
        reset();
        String name = goal.getName();
        double value = goal.getValue();
        double target = goal.getTarget();
        String imageUrl = goal.getImageUrl();
        String localImageUri = goal.getLocalImageUri();
        boolean hasLocalUri = goal.hasLocalImageUri();

        // Clamp progress to 0 <= progress <= 100
        int progress = (int) ((value / target) * 100);
        progress = Math.min(Math.max(0, progress), 100);

        titleTextView.setText(name);
        arcProgressBar.setProgress(progress);
        currentTextView.setText(CurrencyHelper.format(value));
        totalTextView.setText(context.getString(R.string.of_target_amount, CurrencyHelper.format(target)));

        // Prefer a local imageView. Some conversations set the imageView from phone storage, and others
        // rely on the remote imageView.
        if (hasLocalUri)
            image.setImageURI(localImageUri);
        else
            image.setImageURI(imageUrl);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo When an item is clicked the app should navigate to that goal
            }
        });
    }

    private void reset() {
        titleTextView.setText("");
        arcProgressBar.setProgress(0);
        currentTextView.setText("");
        totalTextView.setText("");
        image.clearAnimation();
        itemView.setOnClickListener(null);

    }
}
