package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.exceptions.BotCallbackRequired;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.GoalGalleryAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Gallery of predefined Goals to use as prototypes for Goal creation.
 *
 * The Goal prototype gallery is of type Node because it had to be displayed with a quick answer.
 * This is no longer the case, and it can potentially be changed to an Answer type.
 *
 * Created by Wimpie Victor on 2016/11/24.
 */

public class GoalGalleryViewHolder extends BaseBotViewHolder<Node> {

    private static final String TAG = GoalGalleryViewHolder.class.getName();

    @BindView(R.id.item_view_bot_carousel_recycler_view)
    RecyclerView recyclerView;

    @Inject
    GoalManager goalManager;

    private Handler handler;
    private GoalGalleryAdapter galleryAdapter;
    private BotAdapter botAdapter;
    private HashtagView.TagsClickListener listener;

    public GoalGalleryViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);
        this.handler = new Handler(Looper.getMainLooper());
        this.botAdapter = botAdapter;
        this.listener = tagsClickListener;

        if (!this.botAdapter.hasController())
            throw new BotCallbackRequired(String.format("%s requires adapter to have callback", TAG));

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        galleryAdapter = new GoalGalleryAdapter(listener);
        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void populate(Node model) {
        super.populate(model);
        galleryAdapter.setDataModel(model);
        galleryAdapter.replace((List<GoalPrototype>) this.botAdapter.getController().getObject(BotObjectType.GOAL_PROTOTYPES));
    }

    @Override
    protected void populateModel() {

    }
}
