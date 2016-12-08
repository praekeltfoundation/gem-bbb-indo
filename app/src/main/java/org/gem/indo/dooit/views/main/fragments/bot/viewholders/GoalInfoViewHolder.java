package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.controllers.BotParamType;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.exceptions.BotCallbackRequired;
import org.gem.indo.dooit.views.custom.ArcProgressBar;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class GoalInfoViewHolder extends BaseBotViewHolder<Node> {

    private static final String TAG = GoalInfoViewHolder.class.getName();

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
    @Inject
    Persisted persisted;

    private BotAdapter botAdapter;

    public GoalInfoViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));

        if (!botAdapter.hasController())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        String name = dataModel.values.getString(BotParamType.GOAL_NAME.getKey());
        double value = dataModel.values.getDouble(BotParamType.GOAL_VALUE.getKey());
        double target = dataModel.values.getDouble(BotParamType.GOAL_TARGET.getKey());
        String imageUrl = dataModel.values.getString(BotParamType.GOAL_IMAGE_URL.getKey());
        String localImageUri = dataModel.values.getString(BotParamType.GOAL_LOCAL_IMAGE_URI.getKey());
        boolean hasLocalUri = dataModel.values.getBoolean(BotParamType.GOAL_HAS_LOCAL_IMAGE_URI.getKey());

        // Clamp progress to 0 <= progress <= 100
        int progress = (int) ((value / target) * 100);
        progress = Math.min(Math.max(0, progress), 100);

        titleTextView.setText(name);
        arcProgressBar.setProgress(progress);
        currentTextView.setText(CurrencyHelper.format(value));
        totalTextView.setText(getContext().getString(R.string.of_target_amount, CurrencyHelper.format(target)));

        // Prefer a local image. Some conversations set the image from phone storage, and others
        // rely on the remote image.
        if (hasLocalUri)
            image.setImageURI(localImageUri);
        else
            image.setImageURI(imageUrl);
    }

    @Override
    protected void populateModel() {
        Goal goal = (Goal) botAdapter.getController().getObject();
        dataModel.values.put(BotParamType.GOAL_NAME.getKey(), goal.getName());
        dataModel.values.put(BotParamType.GOAL_VALUE.getKey(), goal.getValue());
        dataModel.values.put(BotParamType.GOAL_TARGET.getKey(), goal.getTarget());
        dataModel.values.put(BotParamType.GOAL_IMAGE_URL.getKey(), goal.getImageUrl());
        dataModel.values.put(BotParamType.GOAL_LOCAL_IMAGE_URI.getKey(), goal.getLocalImageUri());
        dataModel.values.put(BotParamType.GOAL_HAS_LOCAL_IMAGE_URI.getKey(), goal.hasLocalImageUri());
    }
}
